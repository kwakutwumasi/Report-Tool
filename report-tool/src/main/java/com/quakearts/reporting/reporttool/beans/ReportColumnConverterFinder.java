package com.quakearts.reporting.reporttool.beans;

import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.webapp.orm.query.criteria.CriteriaMap;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.model.ReportColumnConverter;

public class ReportColumnConverterFinder {	

	public List<ReportColumnConverter> findObjects(CriteriaMap parameters, QueryOrder...orders){
		return DataStoreFactory.getInstance().getDataStore()
				.find(ReportColumnConverter.class)
				.using(parameters)
				.orderBy(orders)
				.thenList();
	}
	
	public ReportColumnConverter getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(ReportColumnConverter.class,id);
	}
	
	public List<ReportColumnConverter> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().find(ReportColumnConverter.class)
				.using(CriteriaMapBuilder.createCriteria().requireAnyOfTheFollowing()
				.property("converterClass").mustBeLike(searchString)
				.property("positions").mustBeLike(searchString)
				.finish()).thenList();
	}
}
