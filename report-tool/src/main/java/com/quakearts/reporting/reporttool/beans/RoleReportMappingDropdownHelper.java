package com.quakearts.reporting.reporttool.beans;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import com.quakearts.reporting.reporttool.model.RoleReportMapping;

public class RoleReportMappingDropdownHelper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -901809347212270378L;

	private transient RoleReportMappingFinder finder = new RoleReportMappingFinder();
	
	private List<RoleReportMapping> foundItems;
	public List<RoleReportMapping> getFoundItems() {
    		return foundItems;
    }
    	
	private RoleReportMapping roleReportMapping;
	
	public RoleReportMapping getRoleReportMapping(){
		if(roleReportMapping == null){
			roleReportMapping = new RoleReportMapping();
			addToFoundItemsList(roleReportMapping);
		}
		
		return roleReportMapping;
	}
	
	private boolean inCreateMode;
	
	public boolean isInCreateMode() {
		return inCreateMode;
	}

	public void setInCreateMode(boolean inCreateMode) {
		this.inCreateMode = inCreateMode;
	}

	public void addToFoundItemsList(RoleReportMapping roleReportMapping) {
		if(foundItems==null)
			foundItems = new ArrayList<>();
		
		if(!foundItems.contains(roleReportMapping))
			foundItems.add(roleReportMapping);
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
