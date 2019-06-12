package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import static com.quakearts.webapp.orm.query.helper.ParameterMapBuilder.createParameters;

public class ReportQueryFinder {	

	public List<ReportQuery> findObjects(Map<String, Serializable> parameters,QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().list(ReportQuery.class, parameters, queryOrders);
	}
	public ReportQuery getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(ReportQuery.class,id);
	}
	public List<ReportQuery> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().list(ReportQuery.class, createParameters().disjoin()
															.addVariableString("dataSourceJndiName", searchString)
															.addVariableString("name", searchString)
															.addVariableString("sql", searchString)
															.build());
	}
}
