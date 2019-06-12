package com.quakearts.reporting.reporttool.configuration.impl;

import javax.sql.XADataSource;

import org.postgresql.xa.PGXADataSource;

import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField.DataSourceFieldType;

public class PostgresDataSourceConfigurator extends DataSourceConfiguratorBase {

	@Override
	protected DataSourceConfigField[] getCommonConfigFieldsInternal() {
		return new DataSourceConfigField[]{
			new DataSourceConfigField("Server DNS Name/IP Address","serverName", DataSourceFieldType.STRING),
			new DataSourceConfigField("Port","portNumber", DataSourceFieldType.INTEGER),
			new DataSourceConfigField("Database Name","databaseName", DataSourceFieldType.STRING),
			new DataSourceConfigField("User","user", DataSourceFieldType.STRING),
			new DataSourceConfigField("Password","password", DataSourceFieldType.PASSWORD)
		};
	}

	@Override
	protected Class<? extends XADataSource> getDataSourceClassInternal() {
		return PGXADataSource.class;
	}
}
