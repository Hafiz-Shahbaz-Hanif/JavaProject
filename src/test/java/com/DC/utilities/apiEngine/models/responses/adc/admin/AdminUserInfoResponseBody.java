package com.DC.utilities.apiEngine.models.responses.adc.admin;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class AdminUserInfoResponseBody {

	public Map<String, Object> properties; 

	public AdminUserInfoResponseBody(){ 
		properties = new HashMap<>(); 
	}  

	public Map<String, Object> getProperties(){ 
		return properties; 
	} 

	@JsonAnySetter 
	public void add(String property, Object value){ 
		properties.put(property, value); 
	} 

}