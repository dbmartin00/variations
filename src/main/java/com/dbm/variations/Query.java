package com.dbm.variations;

public class Query {

	String token;
	private String uid;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public Query(String token, String uid) {
		this.token = token;
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public void setId(String uid) {
		this.uid = uid;
	}
	
}

