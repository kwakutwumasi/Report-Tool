package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.event.AjaxBehaviorEvent;

import com.quakearts.reporting.reporttool.model.ReportQuery.QueryType;
import com.quakearts.reporting.reporttool.model.ReportQueryParameter.ParameterType;

@Named("reportapp")
@ViewScoped
public class ReportApplicationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8284508073388972949L;
	private static Logger log = Logger.getLogger(ReportApplicationBean.class.getName());
	private String mode;
	private TimeStampConverter converter;
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public Converter getConverter() {
		if(converter==null){
			converter = new TimeStampConverter();
			converter.setPattern("dd/MM/yyyy");
		}
		return converter;
	}
	
	private boolean helpTipsActive;
	
	public boolean isHelpTipsActive() {
		return helpTipsActive;
	}
	
	public void toggleHelpTips(AjaxBehaviorEvent event){
		helpTipsActive = !helpTipsActive;
	}
	
	public static class TimeStampConverter extends DateTimeConverter implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5566188389528245727L;

		@Override
		public Object getAsObject(FacesContext context, UIComponent component, String dateString) {
			Object result;
			try {
				result = super.getAsObject(context, component, dateString);
				if (result instanceof Date) {
					result = new java.sql.Timestamp(((Date) result).getTime());
				}
			} catch (ConverterException ex) {
				log.severe("Exception of type " + ex.getClass().getName()
						+ " was thrown. Message is " + ex.getMessage());
				return null;
			}
			return result;
		}

		@Override
		public String getAsString(FacesContext context, UIComponent component, Object dateObject) {
			String result = null;
			try {
				result = super.getAsString(context, component, dateObject);
			} catch (ConverterException ex) {
				return null;
			}
			return result;
		}
	}

	public QueryType[] getQueryTypes() {
		return QueryType.values();
	}

	public ParameterType[] getParameterTypes() {
		return ParameterType.values();
	}
}
