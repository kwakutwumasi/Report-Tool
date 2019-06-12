package com.quakearts.reporting.reporttool.beans;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quakearts.appbase.exception.ConfigurationException;
import com.quakearts.appbase.internal.json.Json;
import com.quakearts.appbase.internal.json.JsonObject;
import com.quakearts.appbase.internal.json.WriterConfigs;
import com.quakearts.reporting.reporttool.configuration.DataSourceConfigurator;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField;
import com.quakearts.reporting.reporttool.enums.DataSourceType;
import com.quakearts.reporting.reporttool.services.DataSourceService;
import com.quakearts.reporting.reporttool.services.model.DataSourceDisplay;
import com.quakearts.webapp.facelets.base.BaseBean;

@Named("dataSourceConfiguration")
@ViewScoped
public class DataSourceConfigurationPage extends BaseBean {

	private static final String INVALID_SELECTION = "Invalid Selection";
	private static final String DS_JSON = ".ds.json";
	private static final String XA = "xa.";
	/**
	 * 
	 */
	private static final long serialVersionUID = 3811289649594856006L;
	private static final Logger log = LoggerFactory.getLogger(DataSourceConfigurationPage.class);
	
	@Inject
	private transient DataSourceService dataSourceService;
	
	private DataSourceType dataSourceType;
	
	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public DataSourceType[] getDataSourceTypes(){
		return DataSourceType.values();
	}
	
	private boolean inReplaceMode;
	
	public boolean isInReplaceMode() {
		return inReplaceMode;
	}

	public void setInReplaceMode(boolean inReplaceMode) {
		this.inReplaceMode = inReplaceMode;
	}

	public List<DataSourceDisplay> getJNDIDataSources(){
		return dataSourceService.listDataSources();
	}

	private String copyDataSourceJndiName;
	
	public String getCopyDataSourceJndiName() {
		return copyDataSourceJndiName;
	}
	
	public void setCopyDataSourceJndiName(String copyDataSourceJndiName) {
		this.copyDataSourceJndiName = copyDataSourceJndiName;
	}
	
	private transient DataSourceConfigurator dataSourceConfigurator;
	
	public DataSourceConfigurator getDataSourceConfigurator() {
		if(dataSourceConfigurator == null 
				&& dataSourceType != null){
			dataSourceConfigurator = dataSourceType.createDataSourceConfigurator();
		}
		return dataSourceConfigurator;
	}
	
	public void copyDataSource(ActionEvent field){
		if(dataSourceType==null){
			addError(INVALID_SELECTION, "Select a data source type to continue", FacesContext.getCurrentInstance());
			return;
		}
		
		if(isEmpty(copyDataSourceJndiName)){
			addError(INVALID_SELECTION, "Select a data source name to continue", FacesContext.getCurrentInstance());
			return;
		}
		
		try {
			getDataSourceConfigurator().copyDataSource(copyDataSourceJndiName);
		} catch (NamingException e) {
			addError("System Error", "There was an error copying the data source: "+e.getMessage(), 
					FacesContext.getCurrentInstance());
		}
	}
	
	public void saveDataSource(ActionEvent actionEvent){
		JsonObject jsonConfig = Json.object();
		
		if(dataSourceType == null
				|| isEmpty(getDataSourceConfigurator().getDataSourceName())){
			addError(INVALID_SELECTION, "All required fields must be populated before submission", FacesContext.getCurrentInstance());
			return;
		}
		
		jsonConfig.set("datasource.class", getDataSourceConfigurator().getDataSourceClass());
		jsonConfig.set("datasource.name", getDataSourceConfigurator().getDataSourceName());
		
		for(DataSourceConfigField field:getDataSourceConfigurator()
				.getCommonConfigFields()){
			processFields(jsonConfig, field);
		}
		
		if(getDataSourceConfigurator()
				.isInAdvancedMode()){
			for(DataSourceConfigField field:getDataSourceConfigurator()
					.getAdvancedFields()){
				processFields(jsonConfig, field);
			}
		}
		
		setCommonProperties(jsonConfig, getDataSourceConfigurator());
		
		StringWriter stringWriter = new StringWriter();
		
		try {
			jsonConfig.writeTo(stringWriter, WriterConfigs.PRETTY_PRINT);
			dataSourceService.createDataSource(stringWriter.toString(), getDataSourceConfigurator().getDataSourceName().toLowerCase()+DS_JSON, inReplaceMode);
			addMessage("Success", "Data source has been created", FacesContext.getCurrentInstance());
		} catch (IOException|ConfigurationException e) {
			log.error("Unable to save dataSource config file "+getDataSourceConfigurator().getDataSourceName().toLowerCase()+DS_JSON, e);
			addError("Invalid Input", "Unable to create data source file: "+e.getMessage(), FacesContext.getCurrentInstance());
		}
		dataSourceConfigurator = null;
	}

	private void setCommonProperties(JsonObject jsonConfig, DataSourceConfigurator dataSourceConfigurator) {
		if(dataSourceConfigurator.getBorrowConnectionTimeout()>0){
			jsonConfig.set("borrowConnectionTimeout", dataSourceConfigurator.getBorrowConnectionTimeout());
		}
		
		if(dataSourceConfigurator.getLoginTimeout()>0){
			jsonConfig.set("loginTimeout", dataSourceConfigurator.getLoginTimeout());
		}
		
		if(dataSourceConfigurator.getMaintenanceInterval()>0){
			jsonConfig.set("maintenanceInterval", dataSourceConfigurator.getMaintenanceInterval());
		}

		if(dataSourceConfigurator.getMaxIdleTime()>0){
			jsonConfig.set("maxIdleTime", dataSourceConfigurator.getMaxIdleTime());
		}
		
		if(dataSourceConfigurator.getMaxLifetime()>0){
			jsonConfig.set("maxLifetime", dataSourceConfigurator.getMaxLifetime());
		}
		
		if(dataSourceConfigurator.getMaxPoolSize()>0){
			jsonConfig.set("maxPoolSize", dataSourceConfigurator.getMaxPoolSize());
		}

		if(dataSourceConfigurator.getMinPoolSize()>0){
			jsonConfig.set("minPoolSize", dataSourceConfigurator.getMinPoolSize());
		}
		
		if(dataSourceConfigurator.getReapTimeout()>0){
			jsonConfig.set("reapTimeout", dataSourceConfigurator.getReapTimeout());
		}
		
		if(dataSourceConfigurator.getTestQuery() != null){
			jsonConfig.set("testQuery", dataSourceConfigurator.getTestQuery());
		}
	}

	private boolean isEmpty(String string) {
		return string == null || string.trim().isEmpty();
	}

	private void processFields(JsonObject jsonConfig, DataSourceConfigField field) {
		if(field.getValue()!=null){
			switch (field.getType()) {
			case INTEGER:
				jsonConfig.set(XA+field.getPropertyName(), (Integer) field.getValue());
				break;
			case DOUBLE:
				jsonConfig.set(XA+field.getPropertyName(), (Double) field.getValue());
				break;
			case LONG:
				jsonConfig.set(XA+field.getPropertyName(), field.getValue().toString()+"l");
				break;
			case BOOLEAN:
				jsonConfig.set(XA+field.getPropertyName(), (Boolean) field.getValue());
				break;					
			default:
				jsonConfig.set(XA+field.getPropertyName(), field.getValue().toString());
				break;
			}
		}
	}
}
