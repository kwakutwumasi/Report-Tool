package com.quakearts.reporting.reporttool.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import com.quakearts.reporting.reporttool.beans.ReportQueryParameterBean;
import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.generator.Generator;
import com.quakearts.reporting.reporttool.generator.GeneratorState;
import com.quakearts.reporting.reporttool.model.ReportQuery;

public interface ReportQueryService {
	void run(ReportQuery reportQuery, List<ReportQueryParameterBean> parameters, Generator generator, GeneratorState state)
			throws SQLException, NamingException, IOException, GeneratorException;
}
