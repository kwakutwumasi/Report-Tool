package com.quakearts.reporting.reporttool.generator;

import java.util.HashMap;

public class GeneratorState extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6375099106151141245L;

	@SuppressWarnings("unchecked")
	public <T> T get(String key){
		return (T) super.get(key);
	}
}
