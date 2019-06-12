package com.quakearts.reporting.reporttool.configuration.impl;

import javax.sql.XADataSource;

import com.mysql.cj.jdbc.MysqlXADataSource;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField.DataSourceFieldType;

public class MYSQLDataSourceConfigurator extends DataSourceConfiguratorBase {

	@Override
	protected DataSourceConfigField[] getCommonConfigFieldsInternal() {
		return new DataSourceConfigField[]{
			new DataSourceConfigField("Server DNS Name/IP Address", "serverName", DataSourceFieldType.STRING),
			new DataSourceConfigField("Port", "port", DataSourceFieldType.INTEGER),
			new DataSourceConfigField("Database Name","databaseName", DataSourceFieldType.STRING),
			new DataSourceConfigField("User", "user", DataSourceFieldType.STRING),
			new DataSourceConfigField("Password", "password", DataSourceFieldType.PASSWORD),
			new DataSourceConfigField("PIN Global Transaction to Connection", 
					"pinGlobalTxToPhysicalConnection", DataSourceFieldType.BOOLEAN)
		};
	}
	
	@Override
	protected Class<? extends XADataSource> getDataSourceClassInternal() {
		return MysqlXADataSource.class;
	}


}
