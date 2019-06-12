package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.reporting.reporttool.model.RoleReportMapping;
import static com.quakearts.webapp.orm.query.helper.ParameterMapBuilder.createParameters;

public class RoleReportMappingFinder {	

	public List<RoleReportMapping> findObjects(Map<String, Serializable> parameters,QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().list(RoleReportMapping.class, parameters, queryOrders);
	}
	public RoleReportMapping getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(RoleReportMapping.class,id);
	}
	public List<RoleReportMapping> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().list(RoleReportMapping.class, createParameters()
															.addVariableString("roleName", searchString)
															.build());
	}
}
