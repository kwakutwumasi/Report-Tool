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
import com.quakearts.reporting.reporttool.model.RoleReportMapping;
import com.quakearts.reporting.reporttool.model.Report;

@Named("roleReportMappingPage")
@ViewScoped
public class RoleReportMappingPage extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1360210538774897428L;

	private static Logger log = Logger.getLogger(RoleReportMappingPage.class.getName());

	private RoleReportMapping roleReportMapping;
	
	@Inject @Named("reportapp")
	private ReportApplicationBean reportapp;
	private transient RoleReportMappingFinder finder = new RoleReportMappingFinder();
			
	public ReportApplicationBean getReportapp(){
		if(reportapp == null)
			reportapp = new ReportApplicationBean();
			
		return reportapp;
	}
	
	public RoleReportMapping getRoleReportMapping() {
		if(roleReportMapping==null){			if(hasParameter("roleReportMapping")){
				setRoleReportMapping(finder.getById(getParameterInt("roleReportMapping")));
				reportapp.setMode("read");
			} else {
	    			roleReportMapping = new RoleReportMapping();
			}
		}
		
		return roleReportMapping;
	}
    	
	public void setRoleReportMapping(RoleReportMapping roleReportMapping) {
		this.roleReportMapping = roleReportMapping;
		if(roleReportMapping!=null){
			Report report = roleReportMapping.getReport();
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
	
	private List<RoleReportMapping> roleReportMappingList;
	
	public List<RoleReportMapping> getRoleReportMappingList(){
		return roleReportMappingList;
	}
	
	public void findRoleReportMapping(ActionEvent event){
		ParameterMapBuilder parameterBuilder = new ParameterMapBuilder();
		if(roleReportMapping.getReport() != null){
			parameterBuilder.add("report", roleReportMapping.getReport());
		}
		if(roleReportMapping.getRoleName() != null && ! roleReportMapping.getRoleName().trim().isEmpty()){
			parameterBuilder.addVariableString("roleName", roleReportMapping.getRoleName());
		}
		if(roleReportMapping.isValid()){
			parameterBuilder.add("valid", roleReportMapping.isValid());
		}
    		
		try {
			roleReportMappingList = finder.findObjects(parameterBuilder.build());
		} catch (DataStoreException e) {
			addError("Search error", "An error occured while searching for Role Report Mapping", FacesContext.getCurrentInstance());
			log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles searching for RoleReportMapping");
		}		
	}
    	
	public void removeRoleReportMapping(ActionEvent event){
		if(roleReportMapping!=null && roleReportMappingList!=null){
			roleReportMappingList.remove(roleReportMapping);
		}
	}
	
	public boolean isInCreateOrEditMode(){
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().endsWith("create.xhtml") || "edit".equals(reportapp.getMode());
	}
}
