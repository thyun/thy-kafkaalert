package com.skp.kafkaalert.config;

import org.json.JSONObject;

public interface ConfigItem {

	public void init(JSONObject j); 		
	
	public void prepare(); 
	

}