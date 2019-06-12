package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import com.quakearts.reporting.reporttool.model.ReportQuery;

public class ReportQueryDropdownHelper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7530280805160181867L;

	private transient ReportQueryFinder finder = new ReportQueryFinder();
	
	private List<ReportQuery> foundItems;
	public List<ReportQuery> getFoundItems() {
    		return foundItems;
    }
    	
	private ReportQuery reportQuery;
	
	public ReportQuery getReportQuery(){
		if(reportQuery == null){
			reportQuery = new ReportQuery();
			addToFoundItemsList(reportQuery);
		}
		
		return reportQuery;
	}
	
	private boolean inCreateMode;
	
	public boolean isInCreateMode() {
		return inCreateMode;
	}

	public void setInCreateMode(boolean inCreateMode) {
		this.inCreateMode = inCreateMode;
	}

	public void addToFoundItemsList(ReportQuery reportQuery) {
		if(foundItems==null)
			foundItems = new ArrayList<>();
		
		if(!foundItems.contains(reportQuery))
			foundItems.add(reportQuery);
	}
	
	private String searchText;
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	public void filterItems(AjaxBehaviorEvent event){
		if(searchText !=null && !searchText.trim().isEmpty()){
			foundItems = finder.filterByText(searchText);
		}
	}
}
