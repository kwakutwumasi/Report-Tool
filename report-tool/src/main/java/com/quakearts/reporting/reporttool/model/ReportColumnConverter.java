package com.quakearts.reporting.reporttool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class ReportColumnConverter extends IDBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5520612746808152610L;
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private ReportQuery reportQuery;
	@Column(nullable = false)
	private String positions;
	@Column(nullable = false)
	private String converterClass;

	public ReportQuery getReportQuery() {
		return reportQuery;
	}

	public void setReportQuery(ReportQuery reportQuery) {
		this.reportQuery = reportQuery;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getConverterClass() {
		return converterClass;
	}

	public void setConverterClass(String converterClass) {
		this.converterClass = converterClass;
	}

}
