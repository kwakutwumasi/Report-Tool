package com.quakearts.reporting.reporttool.enums;

import javax.enterprise.inject.spi.CDI;

import com.quakearts.reporting.reporttool.generator.Generator;
import com.quakearts.reporting.reporttool.generator.impl.ExcelGenerator;
import com.quakearts.reporting.reporttool.generator.impl.HTMLGenerator;
import com.quakearts.reporting.reporttool.generator.impl.CSVGenerator;

public enum GeneratorType {
	EXCEL(ExcelGenerator.class, "Excel (XLSX)"),
	HTML(HTMLGenerator.class, "Plain"),
	CSV(CSVGenerator.class, "CSV");
	
	private Generator generator;
	private String displayName;

	private GeneratorType(Class<? extends Generator> generatorClass, String displayName) {
		this.generator = CDI.current().select(generatorClass).get();
		this.displayName = displayName;
	}
	
	public Generator getGenerator() {
		return generator;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}