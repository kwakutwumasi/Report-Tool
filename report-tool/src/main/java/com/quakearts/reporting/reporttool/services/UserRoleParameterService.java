package com.quakearts.reporting.reporttool.services;

import com.quakearts.reporting.reporttool.beans.ReportQueryParameterBean;

public interface UserRoleParameterService {
	String resolve(ReportQueryParameterBean bean);
}
