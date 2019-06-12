package com.quakearts.reporting.reporttool.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Report extends IDBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5015398859236035917L;

	@Column(nullable = false, unique = true)
	private String name;
	@Column(nullable = false)
	private String dataSourceJndiName;
	private String header;
	@OneToMany(fetch=FetchType.EAGER, mappedBy = "report")
	@Fetch(FetchMode.SELECT)
	private Set<ReportQuery> reportQueries = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataSourceJndiName() {
		return dataSourceJndiName;
	}

	public void setDataSourceJndiName(String dataSourceJndiName) {
		this.dataSourceJndiName = dataSourceJndiName;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	public Set<ReportQuery> getReportQueries() {
		return reportQueries;
	}
	
	public void setReportQueries(Set<ReportQuery> reportQueries) {
		this.reportQueries = reportQueries;
	}
}
