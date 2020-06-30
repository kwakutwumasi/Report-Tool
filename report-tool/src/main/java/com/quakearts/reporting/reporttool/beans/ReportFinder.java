package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.webapp.orm.query.criteria.CriteriaMap;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.model.Report;

public class ReportFinder {	

	public List<Report> findObjects(CriteriaMap parameters, QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().find(Report.class).using(parameters).orderBy(queryOrders)
				.thenList();
	}
	public Report getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(Report.class,id);
	}
	public List<Report> filterByText(String searchString) {
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().find(Report.class).using(CriteriaMapBuilder
					.createCriteria().requireAnyOfTheFollowing()
					.property("dataSourceJndiName").mustBeLike(searchString)
					.property("header").mustBeLike(searchString)
					.property("name").mustBeLike(searchString)
					.finish())
				.thenList();
	}
}
