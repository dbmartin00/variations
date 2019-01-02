package com.dbm.variations;

public class EightcUser {

	private String username;
	private String uid;
	
	public EightcUser() { }
	
	public String getUid() {
		return uid;
	}
	public void setUid(String id) {
		this.uid = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "UsernameId [username=" + username + ", uid=" + uid + "]";
	}
	
}
