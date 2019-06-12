package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import com.quakearts.reporting.reporttool.model.Report;

public class ReportDropdownHelper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5540279239466242410L;

	private transient ReportFinder finder = new ReportFinder();
	
	private List<Report> foundItems;
	public List<Report> getFoundItems() {
    		return foundItems;
    }
    	
	private Report report;
	
	public Report getReport(){
		if(report == null){
			report = new Report();
			addToFoundItemsList(report);
		}
		
		return report;
	}
	
	private boolean inCreateMode;
	
	public boolean isInCreateMode() {
		return inCreateMode;
	}

	public void setInCreateMode(boolean inCreateMode) {
		this.inCreateMode = inCreateMode;
	}

	public void addToFoundItemsList(Report report) {
		if(foundItems==null)
			foundItems = new ArrayList<>();
		
		if(!foundItems.contains(report))
			foundItems.add(report);
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
