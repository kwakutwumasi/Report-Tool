package com.quakearts.reporting.reporttool.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class ReportQuery extends IDBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1001712230086736277L;
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	private Report report;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false, length = 5120, name = "sql_query")
	private String sql;
	@Column(nullable = false)
	private QueryType type = QueryType.STATEMENT;	
	public enum QueryType {
		STATEMENT, PARAMETERED, PROCEDURE
	}
	private String dataSourceJndiName;
	@OneToMany(fetch=FetchType.EAGER, mappedBy = "reportQuery")
	@Fetch(FetchMode.SELECT)
	private Set<ReportQueryParameter> reportQueryParameters = new HashSet<>();
	@OneToMany(fetch=FetchType.EAGER, mappedBy = "reportQuery")
	@Fetch(FetchMode.SELECT)
	private Set<ReportColumnConverter> reportColumnConverters = new HashSet<>();

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public QueryType getType() {
		return type;
	}

	public void setType(QueryType type) {
		this.type = type;
	}

	public String getDataSourceJndiName() {
		return dataSourceJndiName;
	}

	public void setDataSourceJndiName(String dataSourceJndiName) {
		this.dataSourceJndiName = dataSourceJndiName;
	}
	
	public Set<ReportQueryParameter> getReportQueryParameters() {
		return reportQueryParameters;
	}
	
	public void setReportQueryParameters(Set<ReportQueryParameter> reportQueryParameters) {
		this.reportQueryParameters = reportQueryParameters;
	}

	public Set<ReportColumnConverter> getReportColumnConverters() {
		return reportColumnConverters;
	}

	public void setReportColumnConverters(Set<ReportColumnConverter> reportColumnConverters) {
		this.reportColumnConverters = reportColumnConverters;
	}
}
