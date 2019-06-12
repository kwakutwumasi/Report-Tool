package com.quakearts.reporting.reporttool.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class RoleReportMapping extends IDBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3891745807217389192L;
	@ManyToOne(optional = false)
	private Report report;
	private String roleName;

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
