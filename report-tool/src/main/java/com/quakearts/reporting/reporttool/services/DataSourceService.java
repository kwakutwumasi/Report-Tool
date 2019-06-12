package com.quakearts.reporting.reporttool.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.reporting.reporttool.services.model.DataSourceDisplay;

public interface DataSourceService {
	Connection getConnection(ReportQuery reportQuery) throws NamingException, SQLException;
	List<DataSourceDisplay> listDataSources();
	void createDataSource(String configJson, String fileName, boolean inReplaceMode) throws IOException;
}
