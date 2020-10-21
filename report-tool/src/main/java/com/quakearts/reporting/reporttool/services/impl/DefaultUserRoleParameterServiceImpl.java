package com.quakearts.reporting.reporttool.services.impl;

import javax.inject.Singleton;

import com.quakearts.reporting.reporttool.beans.ReportQueryParameterBean;
import com.quakearts.reporting.reporttool.services.UserRoleParameterService;

@Singleton
public class DefaultUserRoleParameterServiceImpl implements UserRoleParameterService {

	@Override
	public String resolve(ReportQueryParameterBean bean) {
		return "";
	}

}
