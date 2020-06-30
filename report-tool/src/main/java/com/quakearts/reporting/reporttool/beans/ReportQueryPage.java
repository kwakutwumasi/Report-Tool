package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.exception.DataStoreException;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.reporting.reporttool.model.Report;

@Named("reportQueryPage")
@ViewScoped
public class ReportQueryPage extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1677217360886193445L;

	private static Logger log = Logger.getLogger(ReportQueryPage.class.getName());

	private ReportQuery reportQuery;
	
	@Inject @Named("reportapp")
	private ReportApplicationBean reportapp;
	private transient ReportQueryFinder finder = new ReportQueryFinder();
			
	public ReportApplicationBean getReportapp(){
		if(reportapp == null)
			reportapp = new ReportApplicationBean();
			
		return reportapp;
	}
	
	public ReportQuery getReportQuery() {
		if(reportQuery==null){			if(hasParameter("reportQuery")){
				setReportQuery(finder.getById(getParameterInt("reportQuery")));
				reportapp.setMode("read");
			} else {
	    			reportQuery = new ReportQuery();
			}
		}
		
		return reportQuery;
	}
    	
	public void setReportQuery(ReportQuery reportQuery) {
		this.reportQuery = reportQuery;
		if(reportQuery!=null){
			Report report = reportQuery.getReport();
			if(report!=null){
				getReportDropdownHelper().addToFoundItemsList(report);
			}
		}
	}
	
	private ReportDropdownHelper reportDropdownHelper;

	public ReportDropdownHelper getReportDropdownHelper(){
		if(reportDropdownHelper == null)
			reportDropdownHelper = new ReportDropdownHelper();
			
		return reportDropdownHelper;
	}
	
	private List<ReportQuery> reportQueryList;
	
	public List<ReportQuery> getReportQueryList(){
		return reportQueryList;
	}
	
	public void findReportQuery(ActionEvent event){
		CriteriaMapBuilder criteriaBuilder = CriteriaMapBuilder.createCriteria();
		if(reportQuery.getDataSourceJndiName() != null && ! reportQuery.getDataSourceJndiName().trim().isEmpty()){
			criteriaBuilder.property("dataSourceJndiName").mustBeLike(reportQuery.getDataSourceJndiName());
		}
		if(reportQuery.getName() != null && ! reportQuery.getName().trim().isEmpty()){
			criteriaBuilder.property("name").mustBeLike(reportQuery.getName());
		}
		if(reportQuery.getReport() != null){
			criteriaBuilder.property("report").mustBeEqualTo(reportQuery.getReport());
		}
		if(reportQuery.getSql() != null && ! reportQuery.getSql().trim().isEmpty()){
			criteriaBuilder.property("sql").mustBeLike(reportQuery.getSql());
		}
		if(reportQuery.getType() != null){
			criteriaBuilder.property("type").mustBeEqualTo(reportQuery.getType());
		}
		if(reportQuery.isValid()){
			criteriaBuilder.property("valid").mustBeEqualTo(reportQuery.isValid());
		}
    		
		try {
			reportQueryList = finder.findObjects(criteriaBuilder.finish());
		} catch (DataStoreException e) {
			addError("Search error", "An error occured while searching for Report Query", FacesContext.getCurrentInstance());
			log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles searching for ReportQuery");
		}		
	}
    	
	public void removeReportQuery(ActionEvent event){
		if(reportQuery!=null && reportQueryList!=null){
			reportQueryList.remove(reportQuery);
		}
	}
	
	public boolean isInCreateOrEditMode(){
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().endsWith("create.xhtml") || "edit".equals(reportapp.getMode());
	}
}
