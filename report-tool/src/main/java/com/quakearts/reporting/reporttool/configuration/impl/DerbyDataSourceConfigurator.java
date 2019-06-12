package com.quakearts.reporting.reporttool.configuration.impl;

import javax.sql.XADataSource;

import org.apache.derby.jdbc.EmbeddedXADataSource;

import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField.DataSourceFieldType;

public class DerbyDataSourceConfigurator 
	extends DataSourceConfiguratorBase {

	@Override
	protected DataSourceConfigField[] getCommonConfigFieldsInternal() {
		return new DataSourceConfigField[]{
			new DataSourceConfigField("Database Filename", "databaseName", DataSourceFieldType.STRING),
			new DataSourceConfigField("Create Database", "createDatabase", DataSourceFieldType.STRING)
		};
	}

	@Override
	protected Class<? extends XADataSource> getDataSourceClassInternal() {
		return EmbeddedXADataSource.class;
	}

}
