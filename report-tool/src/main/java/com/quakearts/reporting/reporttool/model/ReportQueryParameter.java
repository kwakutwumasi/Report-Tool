package com.quakearts.reporting.reporttool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class ReportQueryParameter extends IDBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3755760623344669216L;
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private ReportQuery reportQuery;
	private String variableName;
	@Column(nullable = false)
	private String displayName;
	@Column(nullable = false)
	private ParameterType type;
	@Column(nullable = false)
	private String positions;

	public enum ParameterType {
		INT, DOUBLE, DATE, DATETIME, STRING, DROPDOWN, AUTOCOMPLETE, CURRENTUSER
	}

	@Column(length = 1024)
	private String extraData;
	@Column(nullable = false)
	private boolean required;

	public ReportQuery getReportQuery() {
		return reportQuery;
	}

	public void setReportQuery(ReportQuery reportQuery) {
		this.reportQuery = reportQuery;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public String getPositions() {
		return positions;
	}
	
	public void setPositions(String positions) {
		this.positions = positions;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
