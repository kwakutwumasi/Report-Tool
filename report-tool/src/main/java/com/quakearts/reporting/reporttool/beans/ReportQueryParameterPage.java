package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.Validator;

import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.exception.DataStoreException;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.model.ReportQueryParameter;
import com.quakearts.reporting.reporttool.model.ReportQuery;

@Named("reportQueryParameterPage")
@ViewScoped
public class ReportQueryParameterPage extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1997795497239694233L;

	private static Logger log = Logger.getLogger(ReportQueryParameterPage.class.getName());

	private ReportQueryParameter reportQueryParameter;
	
	@Inject @Named("reportapp")
	private ReportApplicationBean reportapp;
	private transient ReportQueryParameterFinder finder = new ReportQueryParameterFinder();
			
	public ReportApplicationBean getReportapp(){
		if(reportapp == null)
			reportapp = new ReportApplicationBean();
			
		return reportapp;
	}
	
	public ReportQueryParameter getReportQueryParameter() {
		if(reportQueryParameter==null){			
			if(hasParameter("reportQueryParameter")){
				setReportQueryParameter(finder.getById(getParameterInt("reportQueryParameter")));
				reportapp.setMode("read");
			} else {
	    		reportQueryParameter = new ReportQueryParameter();
			}
		}
		
		return reportQueryParameter;
	}
    	
	public void setReportQueryParameter(ReportQueryParameter reportQueryParameter) {
		this.reportQueryParameter = reportQueryParameter;
		if(reportQueryParameter!=null){
			ReportQuery reportQuery = reportQueryParameter.getReportQuery();
			if(reportQuery!=null){
				getReportQueryDropdownHelper().addToFoundItemsList(reportQuery);
			}
		}
	}
	
	private ReportQueryDropdownHelper reportQueryDropdownHelper;

	public ReportQueryDropdownHelper getReportQueryDropdownHelper(){
		if(reportQueryDropdownHelper == null)
			reportQueryDropdownHelper = new ReportQueryDropdownHelper();
			
		return reportQueryDropdownHelper;
	}
	
	private List<ReportQueryParameter> reportQueryParameterList;
	
	public List<ReportQueryParameter> getReportQueryParameterList(){
		return reportQueryParameterList;
	}
	
	public void findReportQueryParameter(ActionEvent event){
		CriteriaMapBuilder criteriaBuilder = CriteriaMapBuilder.createCriteria();
		if(reportQueryParameter.getDisplayName() != null && ! reportQueryParameter.getDisplayName().trim().isEmpty()){
			criteriaBuilder.property("displayName").mustBeLike(reportQueryParameter.getDisplayName());
		}
		if(reportQueryParameter.getExtraData() != null && ! reportQueryParameter.getExtraData().trim().isEmpty()){
			criteriaBuilder.property("extraData").mustBeLike(reportQueryParameter.getExtraData());
		}
		if(reportQueryParameter.getPositions() != null && ! reportQueryParameter.getPositions().trim().isEmpty()){
			criteriaBuilder.property("positions").mustBeLike(reportQueryParameter.getPositions());
		}
		if(reportQueryParameter.getReportQuery() != null){
			criteriaBuilder.property("reportQuery").mustBeEqualTo(reportQueryParameter.getReportQuery());
		}
		if(reportQueryParameter.isRequired()){
			criteriaBuilder.property("required").mustBeEqualTo(reportQueryParameter.isRequired());
		}
		if(reportQueryParameter.getType() != null){
			criteriaBuilder.property("type").mustBeEqualTo(reportQueryParameter.getType());
		}
		if(reportQueryParameter.isValid()){
			criteriaBuilder.property("valid").mustBeEqualTo(reportQueryParameter.isValid());
		}
		if(reportQueryParameter.getVariableName() != null && ! reportQueryParameter.getVariableName().trim().isEmpty()){
			criteriaBuilder.property("variableName").mustBeLike(reportQueryParameter.getVariableName());
		}

		try {
			reportQueryParameterList = finder.findObjects(criteriaBuilder.finish());
		} catch (DataStoreException e) {
			addError("Search error", "An error occured while searching for Report Query Parameter", FacesContext.getCurrentInstance());
			log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles searching for ReportQueryParameter");
		}		
	}
    	
	public void removeReportQueryParameter(ActionEvent event){
		if(reportQueryParameter!=null && reportQueryParameterList!=null){
			reportQueryParameterList.remove(reportQueryParameter);
		}
	}
	
	public boolean isInCreateOrEditMode(){
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().endsWith("create.xhtml") || "edit".equals(reportapp.getMode());
	}
	
	private transient Validator positionsValidator = new PositionsValidator();
	
	public Validator getPositionsValidator() {
		return positionsValidator;
	}
}
