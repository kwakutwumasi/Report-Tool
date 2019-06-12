package com.quakearts.reporting.reporttool.configuration.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingException;
import javax.sql.XADataSource;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.quakearts.appbase.exception.ConfigurationException;
import com.quakearts.appbase.spi.factory.JavaNamingDirectorySpiFactory;
import com.quakearts.reporting.reporttool.configuration.DataSourceConfigurator;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField.DataSourceFieldType;

public abstract class DataSourceConfiguratorBase implements DataSourceConfigurator {

	private boolean inAdvancedMode;
	protected DataSourceConfigField[] commonFields;
	protected DataSourceConfigField[] advancedFields;
	private String dataSourceName;
	protected int borrowConnectionTimeout;
	protected int loginTimeout;
	protected int maintenanceInterval;
	protected int maxIdleTime;
	protected int maxLifetime;
	protected int maxPoolSize;
	protected int minPoolSize;
	protected int reapTimeout;
	protected String testQuery;
	private BeanInfo info;
	private Set<String> commonProperty;
	
	@Override
	public String getDataSourceName() {
		return dataSourceName;
	}

	@Override
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	@Override
	public int getBorrowConnectionTimeout() {
		return borrowConnectionTimeout;
	}

	@Override
	public void setBorrowConnectionTimeout(int borrowConnectionTimeout) {
		this.borrowConnectionTimeout = borrowConnectionTimeout;
	}

	@Override
	public int getLoginTimeout() {
		return loginTimeout;
	}

	@Override
	public void setLoginTimeout(int loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	@Override
	public int getMaintenanceInterval() {
		return maintenanceInterval;
	}

	@Override
	public void setMaintenanceInterval(int maintenanceInterval) {
		this.maintenanceInterval = maintenanceInterval;
	}

	@Override
	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	@Override
	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	@Override
	public int getMaxLifetime() {
		return maxLifetime;
	}

	@Override
	public void setMaxLifetime(int maxLifetime) {
		this.maxLifetime = maxLifetime;
	}

	@Override
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	@Override
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	@Override
	public int getMinPoolSize() {
		return minPoolSize;
	}

	@Override
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	@Override
	public int getReapTimeout() {
		return reapTimeout;
	}

	@Override
	public void setReapTimeout(int reapTimeout) {
		this.reapTimeout = reapTimeout;
	}

	@Override
	public String getTestQuery() {
		return testQuery;
	}

	@Override
	public void setTestQuery(String testQuery) {
		this.testQuery = testQuery;
	}
	
	@Override
	public DataSourceConfigField[] getCommonConfigFields() {
		if(commonFields==null){
			commonFields = getCommonConfigFieldsInternal();
		}
		return commonFields;
	}
	
	protected abstract DataSourceConfigField[] getCommonConfigFieldsInternal();

	@Override
	public DataSourceConfigField[] getAdvancedFields() {
		if(advancedFields==null){
			loadInfoIfNecessary();
			loadCommonPropertyIfNecessary();
			Set<DataSourceConfigField> fields =  new HashSet<>();
			
			for(PropertyDescriptor descriptor:info.getPropertyDescriptors()){
				if(descriptor.getWriteMethod()!=null 
						&& !commonProperty.contains(descriptor.getName())){
					addTo(fields, descriptor);
				}
			}
			
			advancedFields = fields.toArray(new DataSourceConfigField[fields.size()]);
		}
		
		return advancedFields;
	}

	private void loadCommonPropertyIfNecessary() {
		if(commonProperty == null){
			DataSourceConfigField[] commonFieldsTemp = getCommonConfigFields();
			commonProperty = new HashSet<>();
			for(DataSourceConfigField configField:commonFieldsTemp){
				commonProperty.add(configField.getPropertyName());
			}
		}
	}

	private void loadInfoIfNecessary() {
		if(info==null){
			try {
				info = Introspector.getBeanInfo(getDataSourceClassInternal());
			} catch (IntrospectionException e) {
				throw new ConfigurationException(e);
			}
		}
	}

	private void addTo(Set<DataSourceConfigField> fields, PropertyDescriptor descriptor) {
		DataSourceFieldType type = enumFromType(descriptor.getPropertyType());
		if(type!=null){
			fields.add(new DataSourceConfigField(displayNameFromName(descriptor.getName()),
				descriptor.getName(),type));
		}
	}

	private String displayNameFromName(String name) {
		StringBuilder builder = new StringBuilder();
		char[] chars = name.toCharArray();
		boolean start=true;
		for(char c:chars){
			if(start){
				builder.append(Character.toUpperCase(c));
				start=false;
			}else{
				if(Character.isUpperCase(c))
					builder.append(" ");
				builder.append(c);
			}
		}
		
		return builder.toString();
	}

	private DataSourceFieldType enumFromType(Class<?> propertyType) {
		if(propertyType == String.class){
			return DataSourceFieldType.STRING;
		} else if(propertyType == Boolean.class || propertyType == boolean.class){
			return DataSourceFieldType.BOOLEAN;
		} else if(propertyType == Integer.class || propertyType == int.class){
			return DataSourceFieldType.INTEGER;
		} else if(propertyType == Double.class || propertyType == double.class){
			return DataSourceFieldType.DOUBLE;
		} else if(propertyType == Long.class || propertyType == long.class){
			return DataSourceFieldType.LONG;
		}
		return null;
	}

	@Override
	public void setInAdvancedMode(boolean inAdvancedMode) {
		this.inAdvancedMode = inAdvancedMode;
	}

	@Override
	public boolean isInAdvancedMode() {
		return inAdvancedMode;
	}
	
	@Override
	public String getDataSourceClass() {
		return getDataSourceClassInternal().getName();
	}
	
	@Override
	public void copyDataSource(String jndiName) throws NamingException {
		try {
			if(!jndiName.startsWith("java:/jdbc/"))
				throw new NamingException("The jndiName must start with java:/jdbc/");
			
			AtomikosDataSourceBean dataSource = (AtomikosDataSourceBean) JavaNamingDirectorySpiFactory
					.getInstance().getJavaNamingDirectorySpi()
					.getInitialContext().lookup(jndiName);
			
			if(!dataSource.getXaDataSourceClassName().equals(getDataSourceClass()))
				throw new NamingException("The data source class name does not match");
			
			borrowConnectionTimeout = dataSource.getBorrowConnectionTimeout();
			loginTimeout = dataSource.getLoginTimeout();
			maintenanceInterval = dataSource.getMaintenanceInterval();
			maxIdleTime = dataSource.getMaxIdleTime();
			maxLifetime = dataSource.getMaxLifetime();
			maxPoolSize = dataSource.getMaxPoolSize();
			minPoolSize = dataSource.getMinPoolSize();
			reapTimeout = dataSource.getReapTimeout();
			testQuery = dataSource.getTestQuery();
			
			dataSourceName = jndiName.substring(10);
			
			Map<String, DataSourceConfigField> commonFieldMapping = new HashMap<>();
			for(DataSourceConfigField field : getCommonConfigFields()){
				commonFieldMapping.put(field.getPropertyName(), field);
			}
			
			Map<String, DataSourceConfigField> advancedFieldMapping = new HashMap<>();
			for(DataSourceConfigField field : getAdvancedFields()){
				commonFieldMapping.put(field.getPropertyName(), field);
			}
			
			for(Entry<Object, Object> entry: dataSource.getXaProperties().entrySet()){
				if(commonFieldMapping.containsKey(entry.getKey())){
					commonFieldMapping.get(entry.getKey()).setValue(entry.getValue());
				} else if(advancedFieldMapping.containsKey(entry.getKey())){
					advancedFieldMapping.get(entry.getKey()).setValue(entry.getValue());
				}
			}
		} catch (ClassCastException | SQLException e) {
			throw new NamingException("Unable to find data source with name "+jndiName+": "+e.getMessage());
		}
	}
	
	protected abstract Class<? extends XADataSource> getDataSourceClassInternal();
}
