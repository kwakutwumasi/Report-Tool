package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.reporting.reporttool.model.ReportQueryParameter;
import static com.quakearts.webapp.orm.query.helper.ParameterMapBuilder.createParameters;

public class ReportQueryParameterFinder {	

	public List<ReportQueryParameter> findObjects(Map<String, Serializable> parameters,QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().list(ReportQueryParameter.class, parameters, queryOrders);
	}
	public ReportQueryParameter getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(ReportQueryParameter.class,id);
	}
	public List<ReportQueryParameter> filterByText(String searchString){
		searchString = "%"+searchString+"%";
		return DataStoreFactory.getInstance().getDataStore().list(ReportQueryParameter.class, createParameters().disjoin()
															.addVariableString("displayName", searchString)
															.addVariableString("extraData", searchString)
															.addVariableString("positions", searchString)
															.addVariableString("variableName", searchString)
															.build());
	}
}
