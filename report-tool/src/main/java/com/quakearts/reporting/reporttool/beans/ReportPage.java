package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;
import com.quakearts.webapp.orm.exception.DataStoreException;
import com.quakearts.reporting.reporttool.model.Report;

@Named("reportPage")
@ViewScoped
public class ReportPage extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1575824291829800393L;

	private static Logger log = Logger.getLogger(ReportPage.class.getName());

	private Report report;
	
	@Inject @Named("reportapp")
	private ReportApplicationBean reportapp;
	private transient ReportFinder finder = new ReportFinder();
			
	public ReportApplicationBean getReportapp(){
		if(reportapp == null)
			reportapp = new ReportApplicationBean();
			
		return reportapp;
	}
	
	public Report getReport() {
		if(report==null){			if(hasParameter("report")){
				setReport(finder.getById(getParameterInt("report")));
				reportapp.setMode("read");
			} else {
	    			report = new Report();
			}
		}
		
		return report;
	}
    	
	public void setReport(Report report) {
		this.report = report;
	}
	
	
	private List<Report> reportList;
	
	public List<Report> getReportList(){
		return reportList;
	}
	
	public void findReport(ActionEvent event){
		ParameterMapBuilder parameterBuilder = new ParameterMapBuilder();
		if(report.getDataSourceJndiName() != null && ! report.getDataSourceJndiName().trim().isEmpty()){
			parameterBuilder.addVariableString("dataSourceJndiName", report.getDataSourceJndiName());
		}
		if(report.getHeader() != null && ! report.getHeader().trim().isEmpty()){
			parameterBuilder.addVariableString("header", report.getHeader());
		}
		if(report.getName() != null && ! report.getName().trim().isEmpty()){
			parameterBuilder.addVariableString("name", report.getName());
		}
		if(report.isValid()){
			parameterBuilder.add("valid", report.isValid());
		}
    		
		try {
			reportList = finder.findObjects(parameterBuilder.build());
		} catch (DataStoreException e) {
			addError("Search error", "An error occured while searching for Report", FacesContext.getCurrentInstance());
			log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles searching for Report");
		}		
	}
    	
	public void removeReport(ActionEvent event){
		if(report!=null && reportList!=null){
			reportList.remove(report);
		}
	}
	
	public boolean isInCreateOrEditMode(){
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().endsWith("create.xhtml") || "edit".equals(reportapp.getMode());
	}
}
