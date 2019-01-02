package com.dbm.variations;

public class EmailRegistration {
	
	private String username;
	private String id;
	
	public EmailRegistration(String username, String id) {
		this.setUsername(username);
		this.setId(id);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
