package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.webapp.orm.query.criteria.CriteriaMap;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.model.ReportQuery;

public class ReportQueryFinder {	

	public List<ReportQuery> findObjects(CriteriaMap parameters, QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().find(ReportQuery.class)
				.using(parameters).orderBy(queryOrders).thenList();
	}
	
	public ReportQuery getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(ReportQuery.class,id);
	}
	
	public List<ReportQuery> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().find(ReportQuery.class)
				.using(CriteriaMapBuilder.createCriteria().requireAnyOfTheFollowing()
				.property("dataSourceJndiName").mustBeLike(searchString)
				.property("name").mustBeLike(searchString)
				.property("sql").mustBeLike(searchString)
				.finish())
			.thenList();
	}
}
