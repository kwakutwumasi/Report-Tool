package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quakearts.reporting.reporttool.model.ReportQueryParameter;
import com.quakearts.reporting.reporttool.model.ReportQueryParameter.ParameterType;
import com.quakearts.reporting.reporttool.services.DataSourceService;
import com.quakearts.webapp.facelets.base.BaseBean;

public class ReportQueryParameterBean extends BaseBean implements Comparable<ReportQueryParameterBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 942681395228616938L;

	private static final Logger log = LoggerFactory.getLogger(ReportQueryParameterBean.class);
	
	@Inject
	private DataSourceService service;
	
	private ReportQueryParameter reportQueryParameter;
	private Serializable value;
	private String suggestion;
	private SelectItem[] selectItems;
	
	public ReportQueryParameterBean(ReportQueryParameter reportQueryParameter) {
		this.reportQueryParameter = reportQueryParameter;
		try {
			if(reportQueryParameter.getType() == ParameterType.DROPDOWN){
				String[] tuples = reportQueryParameter.getExtraData().split(";");
				selectItems = new SelectItem[tuples.length];
				int index = 0;
				for(String tuple:tuples){
					String[] splitTuple = tuple.split(":",2);
					selectItems[index++] = new SelectItem(splitTuple[0], splitTuple[1]);
				}
			}			
		} catch (NullPointerException|IndexOutOfBoundsException e) {
			addError("Invalid Value", "The parameter setup is invalid for extravalue '"+reportQueryParameter.getExtraData()
				+"': " + e.getMessage(), FacesContext.getCurrentInstance());
			log.error("Error fetching initial dropdown values for report {} at reportQuery {} at reportQueryParameter {} with extraData as {}", 
					reportQueryParameter.getReportQuery().getReport().getName(),
					reportQueryParameter.getReportQuery().getName(),
					reportQueryParameter.getDisplayName(),
					reportQueryParameter.getExtraData());
			log.error("", e);
		}
	}

	public String getVariableName() {
		return reportQueryParameter.getVariableName();
	}

	public String getDisplayName(){
		return reportQueryParameter.getDisplayName();
	}
	
	public ParameterType getType(){
		return reportQueryParameter.getType();
	}
	
	public boolean isRequired(){
		return reportQueryParameter.isRequired();
	}
	
	public int[] getPositions() throws SQLException {
		try {
			String positionsString = reportQueryParameter.getPositions();
			String[] parts = positionsString.split(",");
			int[] positions = new int[parts.length];
			int index = 0;
			for(String part:parts){
				positions[index] = Integer.parseInt(part);
			}
			
			return positions;
		} catch (NullPointerException | NumberFormatException e) {
			throw new SQLException("Parameter positions cannot be retrieved", e);
		}
	}
	
	public Serializable getValue() {
		return value;
	}
	
	public void setValue(Serializable value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAndConvertValue() {
		return (T) value;
	}

	public boolean isNull(){
		return value == null;
	}
	
	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
	
	public SelectItem[] getSelectItems() {
		return selectItems;
	}
	
	public void autoComplete(AjaxBehaviorEvent actionEvent){
		try(Connection connection = service.getConnection(reportQueryParameter.getReportQuery())) {
			try(PreparedStatement statement = connection.prepareStatement(reportQueryParameter.getExtraData())){
				statement.setString(1, suggestion);
				try(ResultSet resultSet = statement.executeQuery()){
					if(resultSet.next()){
						List<SelectItem> selectItemsArray = new ArrayList<>();
						do {
							SelectItem selectItem = new SelectItem();
							selectItem.setLabel(resultSet.getString(2));
							selectItem.setValue(resultSet.getObject(1));
							selectItemsArray.add(selectItem);
						} while (resultSet.next());
						selectItems = selectItemsArray.toArray(new SelectItem[selectItemsArray.size()]);
					}
				}
			}
		} catch (SQLException | NamingException e) {
			addError("System Error", e.getMessage(), FacesContext.getCurrentInstance());
			log.error("Error fetching autocomplete values", e);
		}
	}
	
	private ReportQueryParameterConverter converter = new ReportQueryParameterConverter();
	
	public Converter getConverter() {
		return converter;
	}
	
	private class ReportQueryParameterConverter implements Converter, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8159225376042527075L;
		private static final String UNABLE_TO_CONVERT_VALUE = "Unable to convert value ";

		@Override
		public Object getAsObject(FacesContext context, UIComponent component, String value) {
			if(value!=null){
				switch (getType()) {
				case INT:
					try {
						return Integer.parseInt(value);
					} catch (NumberFormatException e) {
						throw new ConverterException(UNABLE_TO_CONVERT_VALUE+value+" to an integer");
					}
				case DOUBLE:
					try {
						return Double.parseDouble(value);
					} catch (NumberFormatException e) {
						throw new ConverterException(UNABLE_TO_CONVERT_VALUE+value+" to a decimal");
					}
				case DATE:
					try {
						java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse(value);
						return new java.sql.Date(date.getTime());
					} catch (ParseException e) {
						throw new ConverterException(UNABLE_TO_CONVERT_VALUE+value+" to a date");
					}
				case DATETIME:
					try {
						java.util.Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(value);
						return new java.sql.Timestamp(date.getTime());
					} catch (ParseException e) {
						throw new ConverterException(UNABLE_TO_CONVERT_VALUE+value+" to a date/time value");
					}
				default:
					return value;
				}
			}
			return null;
		}

		@Override
		public String getAsString(FacesContext context, UIComponent component, Object value) {
			if(value!=null){
				switch (getType()) {
				case INT:
					return Integer.toString((Integer)value);
				case DOUBLE:
					return Double.toString((Double)value);
				case DATE:
					return new SimpleDateFormat("dd/MM/yyyy").format(value);					
				case DATETIME:
					return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(value);					
				default:
					return value.toString();
				}
			}
			return null;
		}
		
	}

	@Override
	public int compareTo(ReportQueryParameterBean o) {
		return this.reportQueryParameter.getId() - o.reportQueryParameter.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((converter == null) ? 0 : converter.hashCode());
		result = prime * result + ((reportQueryParameter == null) ? 0 : reportQueryParameter.hashCode());
		result = prime * result + Arrays.hashCode(selectItems);
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((suggestion == null) ? 0 : suggestion.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportQueryParameterBean other = (ReportQueryParameterBean) obj;
		return checkFields(other);
	}

	private boolean checkFields(ReportQueryParameterBean other) {
		try {
			checkConverter(other);
			checkReportQueryParameter(other);
			checkSelectItems(other);
			checkSuggestion(other);
			checkValue(other);
		} catch (NotEqualException e) {
			return false;
		}
		return true;
	}

	private void checkConverter(ReportQueryParameterBean other) throws NotEqualException {
		if (converter == null) {
			if (other.converter != null)
				throw new NotEqualException();
		} else if (!converter.equals(other.converter)){
			throw new NotEqualException();
		}
	}

	private void checkReportQueryParameter(ReportQueryParameterBean other) throws NotEqualException {
		if (reportQueryParameter == null) {
			if (other.reportQueryParameter != null)
				throw new NotEqualException();
		} else if (!reportQueryParameter.equals(other.reportQueryParameter)){
			throw new NotEqualException();
		}
	}

	private void checkSelectItems(ReportQueryParameterBean other) throws NotEqualException {
		if (!Arrays.equals(selectItems, other.selectItems))
			throw new NotEqualException();
		if (service == null) {
			if (other.service != null)
				throw new NotEqualException();
		} else if (!service.equals(other.service)){
			throw new NotEqualException();
		}
	}

	private void checkSuggestion(ReportQueryParameterBean other) throws NotEqualException {
		if (suggestion == null) {
			if (other.suggestion != null)
				throw new NotEqualException();
		} else if (!suggestion.equals(other.suggestion)){
			throw new NotEqualException();
		}
	}

	private void checkValue(ReportQueryParameterBean other) throws NotEqualException {
		if (value == null) {
			if (other.value != null)
				throw new NotEqualException();
		} else if (!value.equals(other.value)) {
			throw new NotEqualException();
		}
	}
	
	public class NotEqualException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
}
