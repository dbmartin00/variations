package com.dbm.variations;

import com.google.gson.JsonObject;

public class Credential {

	String username;
	String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Credential(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String
	toJson() {
		JsonObject result = new JsonObject();
		
		result.addProperty("username", this.username);
		result.addProperty("password", this.password);
		
		return result.toString();
	}
	
}
