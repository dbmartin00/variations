package com.dbm.variations;

import com.google.gson.JsonObject;

public class EightcEvent {

	String eightc;
	long startDate;
	long endDate;
	int valence;
	
	public String getEightc() {
		return eightc;
	}
	public void setEightc(String eightc) {
		this.eightc = eightc;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public int getValence() {
		return valence;
	}
	public void setValence(int valence) {
		this.valence = valence;
	}
	
	public EightcEvent(String eightc, long startDate, long endDate, int valence) {
		this.eightc = eightc;
		this.startDate = startDate;
		this.endDate = endDate;
		this.valence = valence;
	}
	
	public String
	toJson() {
		JsonObject result = new JsonObject();
		
		result.addProperty("eightc", eightc);
		result.addProperty("startDate", startDate);
		result.addProperty("endDate", endDate);
		result.addProperty("value", valence);
		
		return result.toString();
	}
}
