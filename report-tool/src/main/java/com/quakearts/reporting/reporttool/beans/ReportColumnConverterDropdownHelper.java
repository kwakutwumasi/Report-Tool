package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import com.quakearts.reporting.reporttool.model.ReportColumnConverter;

public class ReportColumnConverterDropdownHelper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2517958467568115945L;

	private transient ReportColumnConverterFinder finder = new ReportColumnConverterFinder();
	
	private List<ReportColumnConverter> foundItems;
	public List<ReportColumnConverter> getFoundItems() {
    		return foundItems;
    }
    	
	private ReportColumnConverter reportColumnConverter;
	
	public ReportColumnConverter getReportColumnConverter(){
		if(reportColumnConverter == null){
			reportColumnConverter = new ReportColumnConverter();
			addToFoundItemsList(reportColumnConverter);
		}
		
		return reportColumnConverter;
	}
	
	private boolean inCreateMode;
	
	public boolean isInCreateMode() {
		return inCreateMode;
	}

	public void setInCreateMode(boolean inCreateMode) {
		this.inCreateMode = inCreateMode;
	}

	public void addToFoundItemsList(ReportColumnConverter reportColumnConverter) {
		if(foundItems==null)
			foundItems = new ArrayList<>();
		
		foundItems.add(reportColumnConverter);
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
