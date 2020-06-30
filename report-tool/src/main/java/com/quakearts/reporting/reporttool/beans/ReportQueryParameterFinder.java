package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.webapp.orm.query.criteria.CriteriaMap;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.model.ReportQueryParameter;

public class ReportQueryParameterFinder {	

	public List<ReportQueryParameter> findObjects(CriteriaMap parameters,QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().find(ReportQueryParameter.class)
				.using(parameters).orderBy(queryOrders)
				.thenList();
	}
	
	public ReportQueryParameter getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(ReportQueryParameter.class,id);
	}
	
	public List<ReportQueryParameter> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().find(ReportQueryParameter.class)
				.using(CriteriaMapBuilder.createCriteria().requireAnyOfTheFollowing()
				.property("displayName").mustBeLike(searchString)
				.property("extraData").mustBeLike(searchString)
				.property("positions").mustBeLike(searchString)
				.property("variableName").mustBeLike(searchString)
				.finish())
			.thenList();
	}
}
