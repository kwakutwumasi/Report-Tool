package com.quakearts.reporting.reporttool.configuration.model;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class DataSourceConfigField {
	private String displayName;
	private String propertyName;
	private DataSourceFieldType type;

	public enum DataSourceFieldType {
		INTEGER, LONG, DOUBLE, BOOLEAN, STRING, PASSWORD
	}

	private Object value;
	
	public DataSourceConfigField(String displayName, String propertyName, DataSourceFieldType type) {
		this.displayName = displayName;
		this.propertyName = propertyName;
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public DataSourceFieldType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	private DataSourceConfigFieldConverter converter = new DataSourceConfigFieldConverter();
	
	public DataSourceConfigFieldConverter getConverter() {
		return converter;
	}
	
	private class DataSourceConfigFieldConverter 
		implements Converter {

		@Override
		public Object getAsObject(FacesContext context, UIComponent component, String value) {
			if(type != null && value != null){
				String stringValue = value.toString();
				switch (type) {
				case BOOLEAN:
					return Boolean.parseBoolean(stringValue);
				case DOUBLE:
					return Double.parseDouble(stringValue);
				case INTEGER:
					return Integer.parseInt(stringValue);
				case LONG:
					return Long.parseLong(stringValue);
				default:
					return stringValue;
				}				
			}
			return null;
		}

		@Override
		public String getAsString(FacesContext context, UIComponent component, Object value) {
			if(value != null){
				return value.toString();
			}
			return null;
		}
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		DataSourceConfigField other = (DataSourceConfigField) obj;
		return checkFields(other);
	}

	private boolean checkFields(DataSourceConfigField other) {
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName)){
			return false;
		}
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		} else if (!propertyName.equals(other.propertyName)){
			return false;
		}
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value)){
			return false;
		}
		return true;
	}

}
