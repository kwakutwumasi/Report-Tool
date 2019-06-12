package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.QueryOrder;
import com.quakearts.reporting.reporttool.model.ReportColumnConverter;
import static com.quakearts.webapp.orm.query.helper.ParameterMapBuilder.createParameters;

public class ReportColumnConverterFinder {	

	public List<ReportColumnConverter> findObjects(Map<String, Serializable> parameters,QueryOrder...queryOrders){
		return DataStoreFactory.getInstance().getDataStore().list(ReportColumnConverter.class, parameters, queryOrders);
	}
	public ReportColumnConverter getById(int id){
		return DataStoreFactory.getInstance().getDataStore().get(ReportColumnConverter.class,id);
	}
	public List<ReportColumnConverter> filterByText(String searchString){
		return DataStoreFactory.getInstance().getDataStore().list(ReportColumnConverter.class, createParameters().disjoin()
															.addVariableString("converterClass", searchString)
															.addVariableString("positions", searchString)
															.build());
	}
}
