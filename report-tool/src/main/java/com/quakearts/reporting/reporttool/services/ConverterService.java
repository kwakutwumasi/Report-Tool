package com.quakearts.reporting.reporttool.services;

import java.io.IOException;

import com.quakearts.reporting.reporttool.generator.converter.Converter;
import com.quakearts.reporting.reporttool.model.ReportQuery;

public interface ConverterService {
	Converter getConverter(ReportQuery reportQuery, int index);
	void loadConverter(String fileName, byte[] fileBytes) throws IOException;
	boolean verifyConverterClass(String converterClassName);
}
