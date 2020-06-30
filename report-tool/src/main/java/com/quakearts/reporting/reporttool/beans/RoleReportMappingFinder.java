package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.webapp.orm.query.criteria.CriteriaMap;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.model.RoleReportMapping;

public class RoleReportMappingFinder {	

	public List<RoleReportMapping> findObjects(CriteriaMap parameters, QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().find(RoleReportMapping.class)
				.using(parameters).orderBy(queryOrders)
			.thenList();
	}
	
	public RoleReportMapping getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(RoleReportMapping.class,id);
	}
	
	public List<RoleReportMapping> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore()
				.find(RoleReportMapping.class).using(CriteriaMapBuilder.createCriteria()
						.property("roleName").mustBeLike(searchString)
				.finish()).thenList();
	}
}
