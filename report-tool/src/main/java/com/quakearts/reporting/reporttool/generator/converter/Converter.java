package com.quakearts.reporting.reporttool.generator.converter;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface Converter {
	String convertResult(ResultSet resultSet, int index) throws SQLException;
}
