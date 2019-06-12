package com.quakearts.reporting.reporttool.generator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.quakearts.reporting.reporttool.beans.ReportQueryParameterBean;
import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.model.Report;
import com.quakearts.reporting.reporttool.model.ReportQuery;

public interface Generator {
	GeneratorState createStateForReport(Report report) throws GeneratorException;
	void generate(ReportQuery report, List<ReportQueryParameterBean> parameters, ResultSet resultSet, GeneratorState state) throws SQLException, GeneratorException;
	void writeToResponse(GeneratorState state, HttpServletResponse response) throws GeneratorException;
}
