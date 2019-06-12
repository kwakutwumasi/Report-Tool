package com.quakearts.reporting.reporttool.beans;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class PositionsValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		if(value == null){
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Data", 
					"'Positions' is required"));
		} else if(!value.toString().matches("[\\d]+([\\d,]+)?")) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Data", 
					"'Positions' must consist of integers separated by commas. Example: 1,2,3..."));
		}
	}

}