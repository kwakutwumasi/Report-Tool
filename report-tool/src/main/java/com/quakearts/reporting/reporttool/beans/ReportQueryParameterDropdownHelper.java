package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import com.quakearts.reporting.reporttool.model.ReportQueryParameter;

public class ReportQueryParameterDropdownHelper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6018052641979093951L;

	private transient ReportQueryParameterFinder finder = new ReportQueryParameterFinder();
	
	private List<ReportQueryParameter> foundItems;
	public List<ReportQueryParameter> getFoundItems() {
    		return foundItems;
    }
    	
	private ReportQueryParameter reportQueryParameter;
	
	public ReportQueryParameter getReportQueryParameter(){
		if(reportQueryParameter == null){
			reportQueryParameter = new ReportQueryParameter();
			addToFoundItemsList(reportQueryParameter);
		}
		
		return reportQueryParameter;
	}
	
	private boolean inCreateMode;
	
	public boolean isInCreateMode() {
		return inCreateMode;
	}

	public void setInCreateMode(boolean inCreateMode) {
		this.inCreateMode = inCreateMode;
	}

	public void addToFoundItemsList(ReportQueryParameter reportQueryParameter) {
		if(foundItems==null)
			foundItems = new ArrayList<>();
		
		if(!foundItems.contains(reportQueryParameter))
			foundItems.add(reportQueryParameter);
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
