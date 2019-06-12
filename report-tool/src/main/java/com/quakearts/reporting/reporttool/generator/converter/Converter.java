package com.quakearts.reporting.reporttool.generator.converter;

import java.sql.ResultSet;

@FunctionalInterface
public interface Converter {
	String convertResult(ResultSet resultSet, int index);
}
