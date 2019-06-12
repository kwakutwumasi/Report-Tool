package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import java.util.stream.Collectors;

import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.webapp.facelets.base.BaseBean;

public class ReportQueryBean extends BaseBean implements Comparable<ReportQueryBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6536447878579396131L;
	
	private ReportQuery reportQuery;
	
	private List<ReportQueryParameterBean> beans;

	public ReportQueryBean(ReportQuery reportQuery) {
		this.reportQuery = reportQuery;
		beans = reportQuery.getReportQueryParameters()
				.stream().map(ReportQueryParameterBean::new)
				.sorted().collect(Collectors.toList());
	}
	
	public ReportQuery getReportQuery() {
		return reportQuery;
	}
	
	public List<ReportQueryParameterBean> getBeans() {
		return beans;
	}

	@Override
	public int compareTo(ReportQueryBean o) {
		return this.reportQuery.getId()-o.getReportQuery().getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beans == null) ? 0 : beans.hashCode());
		result = prime * result + ((reportQuery == null) ? 0 : reportQuery.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportQueryBean other = (ReportQueryBean) obj;
		if (beans == null) {
			if (other.beans != null)
				return false;
		} else if (!beans.equals(other.beans))
			return false;
		if (reportQuery == null) {
			if (other.reportQuery != null)
				return false;
		} else if (!reportQuery.equals(other.reportQuery))
			return false;
		return true;
	}
}
