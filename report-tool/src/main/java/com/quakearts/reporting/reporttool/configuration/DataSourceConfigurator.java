package com.quakearts.reporting.reporttool.configuration;

import javax.naming.NamingException;

import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField;

public interface DataSourceConfigurator {
	DataSourceConfigField[] getCommonConfigFields();
	DataSourceConfigField[] getAdvancedFields();
	void setInAdvancedMode(boolean inAdvancedMode);
	boolean isInAdvancedMode();
	String getDataSourceClass();
	void setTestQuery(String testQuery);
	String getTestQuery();
	void setReapTimeout(int reapTimeout);
	int getReapTimeout();
	void setMinPoolSize(int minPoolSize);
	int getMinPoolSize();
	void setMaxPoolSize(int maxPoolSize);
	int getMaxPoolSize();
	void setMaxLifetime(int maxLifetime);
	int getMaxLifetime();
	void setMaxIdleTime(int maxIdleTime);
	int getMaxIdleTime();
	void setMaintenanceInterval(int maintenanceInterval);
	int getMaintenanceInterval();
	void setLoginTimeout(int loginTimeout);
	int getLoginTimeout();
	void setBorrowConnectionTimeout(int borrowConnectionTimeout);
	int getBorrowConnectionTimeout();
	void setDataSourceName(String dataSourceName);
	String getDataSourceName();
	void copyDataSource(String jndiName) throws NamingException;
}
