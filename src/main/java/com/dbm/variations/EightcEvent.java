package com.dbm.variations;

import com.google.gson.JsonObject;

public class EightcEvent {

	String eightc;
	long startDate;
	long endDate;
	int valence;
	String happening;
	
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
	public String getHappening() {
		return happening;
	}
	public void setHappening(String happening) {
		this.happening = happening;
	}
	
	public EightcEvent(String eightc, long startDate, long endDate, int valence, String happening) {
		this.eightc = eightc;
		this.startDate = startDate;
		this.endDate = endDate;
		this.valence = valence;
		this.happening = happening;
	}
	
	public String
	toJson() {
		JsonObject result = new JsonObject();
		
		result.addProperty("eightc", eightc);
		result.addProperty("startDate", startDate);
		result.addProperty("endDate", endDate);
		result.addProperty("value", valence);
		result.addProperty("happening", getHappening());
		
		return result.toString();
	}

}
