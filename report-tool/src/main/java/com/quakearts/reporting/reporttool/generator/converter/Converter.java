package com.quakearts.reporting.reporttool.generator.converter;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.quakearts.reporting.reporttool.generator.GeneratorState;

@FunctionalInterface
public interface Converter {
	String convertResult(GeneratorState state, ResultSet resultSet, int index) throws SQLException;
}
