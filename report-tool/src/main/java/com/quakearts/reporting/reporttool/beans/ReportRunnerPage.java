package com.quakearts.reporting.reporttool.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quakearts.reporting.reporttool.enums.GeneratorType;
import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.generator.Generator;
import com.quakearts.reporting.reporttool.generator.GeneratorState;
import com.quakearts.reporting.reporttool.model.Report;
import com.quakearts.reporting.reporttool.model.RoleReportMapping;
import com.quakearts.reporting.reporttool.services.ReportQueryService;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStore;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.cdi.annotation.DataStoreFactoryHandle;

@Named("reportRunner")
@ViewScoped
public class ReportRunnerPage extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3754586001411651653L;
	private static final Logger log = LoggerFactory.getLogger(ReportRunnerPage.class);

	private List<Report> reports;
	@Inject @DataStoreFactoryHandle
	private transient DataStoreFactory factory;
	
	@Inject
	private transient ReportQueryService reportQueryService;
	
	public List<Report> getReports() {
		if(reports == null){
			FacesContext context = FacesContext.getCurrentInstance();
			DataStore dataStore = factory.getDataStore();
			reports = dataStore.find(RoleReportMapping.class)
					.filterBy("valid").withAValueEqualTo(Boolean.TRUE)
					.thenList().stream().filter(mapping->context.getExternalContext().isUserInRole(mapping.getRoleName()))
					.map(RoleReportMapping::getReport)
					.collect(Collectors.toList());
		}
		return reports;
	}
	
	public GeneratorType[] getGeneratorTypes(){
		return GeneratorType.values();
	}
	
	private GeneratorType generatorType;
	
	public GeneratorType getGeneratorType() {
		return generatorType;
	}
	
	public void setGeneratorType(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}
	
	private Report report;
	
	public Report getReport() {
		return report;
	}
	
	private List<ReportQueryBean> reportQueries;
	private GeneratorState state;

	public void setReport(Report report) {
		if(this.report == null || this.report.getId() != report.getId()){
			this.report = report;
			
			reportQueries = report.getReportQueries()
					.stream().map(ReportQueryBean::new)
					.sorted().collect(Collectors.toList());
		}
	}
	
	public List<ReportQueryBean> getReportQueries() {
		return reportQueries;
	}
	
	public void runReport(ActionEvent event){
		FacesContext context = FacesContext.getCurrentInstance();
		if(report == null){
			addError("Invalid Selection", "Select a report to continue", context);
			return;
		}
		
		if(generatorType == null){
			addError("Invalid Selection", "Select a report type to continue", context);
			return;
		}
		
		Generator generator = generatorType.getGenerator();
		try {
			state = generator.createStateForReport(report);
		} catch (GeneratorException e) {
			addError("System Error", e.getMessage(), context);
			log.error("Unable to generate report {}", report.getName());
			log.error("", e);
			return;
		}
		
		for(ReportQueryBean queryBean:reportQueries){
			try {
				reportQueryService.run(queryBean.getReportQuery(), queryBean.getBeans(), generator, state);
			} catch (SQLException | NamingException | IOException | GeneratorException e) {
				addError("System Error", e.getMessage(), context);
				log.error("Unable to run report query {} for report {}", queryBean.getReportQuery().getName(), report.getName());
				log.error("", e);
				state = null;
				return;
			}
		}
		addMessage("Success", "the report is ready for download", context);
	}
	
	public boolean isDownloadReady(){
		return state!=null;
	}
	
	public void download(ActionEvent event){
		if(state==null)
			return;
			
		FacesContext context = FacesContext.getCurrentInstance();
		context.responseComplete();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		Generator generator = generatorType.getGenerator();
		try {
			generator.writeToResponse(state, response);
		} catch (GeneratorException e) {
			log.error("Unable to generate report {}", report.getName());
			log.error("", e);
		}
		
		state = null;
	}
	
	private ReportQueryParameterBean selectedDateParameterBean;
	
	public void setSelectedDateParameterBean(ReportQueryParameterBean selectedDateParameterBean) {
		this.selectedDateParameterBean = selectedDateParameterBean;
	}
	
	public ReportQueryParameterBean getSelectedDateParameterBean() {
		return selectedDateParameterBean;
	}
	
	private String selectedDate;

	public String getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}
	
	public void setDateParameter(AjaxBehaviorEvent event){
		if(selectedDate != null && selectedDateParameterBean != null){
			selectedDateParameterBean.setValue((Serializable)
					selectedDateParameterBean.getConverter().getAsObject(FacesContext.getCurrentInstance(),
					event.getComponent(), selectedDate));
		}
	}
}
