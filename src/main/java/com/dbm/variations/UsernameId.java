package com.dbm.variations;

public class UsernameId {

	private String username;
	private String id;
	
	public UsernameId() { }
	
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
		return "UsernameId [username=" + username + ", id=" + id + "]";
	}
	
}
