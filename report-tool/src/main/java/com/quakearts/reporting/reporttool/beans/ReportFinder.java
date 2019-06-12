package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.reporting.reporttool.model.Report;
import static com.quakearts.webapp.orm.query.helper.ParameterMapBuilder.createParameters;

public class ReportFinder {	

	public List<Report> findObjects(Map<String, Serializable> parameters,QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().list(Report.class, parameters, queryOrders);
	}
	public Report getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(Report.class,id);
	}
	public List<Report> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().list(Report.class, createParameters().disjoin()
															.addVariableString("dataSourceJndiName", searchString)
															.addVariableString("header", searchString)
															.addVariableString("name", searchString)
															.build());
	}
}
