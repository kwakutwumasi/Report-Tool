package com.quakearts.reporting.reporttool.enums;

import java.util.HashMap;
import java.util.Map;

import com.quakearts.reporting.reporttool.configuration.DataSourceConfigurator;
import com.quakearts.reporting.reporttool.configuration.impl.DerbyDataSourceConfigurator;
import com.quakearts.reporting.reporttool.configuration.impl.MSSQLDataSourceConfigurator;
import com.quakearts.reporting.reporttool.configuration.impl.MYSQLDataSourceConfigurator;
import com.quakearts.reporting.reporttool.configuration.impl.PostgresDataSourceConfigurator;

public enum DataSourceType{
	MYSQL(MYSQLDataSourceConfigurator.class,"MySQL"),
	MSSQL(MSSQLDataSourceConfigurator.class,"Microsoft SQL Server"),
	POSTGRES(PostgresDataSourceConfigurator.class,"Postgres"),
	DERBY(DerbyDataSourceConfigurator.class,"Apache Derby");
	
	private Class<? extends DataSourceConfigurator> dataSourceConfiguratorClass;
	private String displayName;

	private DataSourceType(Class<? extends DataSourceConfigurator> dataSourceConfiguratorClass, String displayName) {
		this.dataSourceConfiguratorClass = dataSourceConfiguratorClass;
		this.displayName = displayName;
	}

	public DataSourceConfigurator createDataSourceConfigurator(){
		try {
			return dataSourceConfiguratorClass.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new UnsupportedOperationException("Cannot get configurator", e);
		}
	}
	
	public String getDisplayName() {
		return displayName;
	}

	private static Map<String, DataSourceType> dataSourceClassNameCache;
	
	public static DataSourceType fromClassName(String className){
		if(dataSourceClassNameCache==null){
			dataSourceClassNameCache  = new HashMap<>();
			for(DataSourceType dataSourceType:values()){
				dataSourceClassNameCache.put(dataSourceType.createDataSourceConfigurator().getDataSourceClass(), dataSourceType);
			}
		}
		
		if(dataSourceClassNameCache.containsKey(className))
			return dataSourceClassNameCache.get(className);
		
		throw new UnsupportedOperationException("Class "+className
				+" does not have a configured enumeration value");
	}
}