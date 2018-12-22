package com.dbm.variations;

public class IdAddress {
	
	private String id;
	private String username;
	
	public IdAddress() {
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "IdAddress [id=" + id + ", username=" + username + "]";
	}
	
	
	

}
