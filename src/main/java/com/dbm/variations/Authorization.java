package com.dbm.variations;

import java.util.Map;
import java.util.TreeMap;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

public class Authorization {

	AmazonDynamoDB ddb;
	
	public boolean
	isAuthorized(String id) {
		boolean result = false;
		if (ddb == null) {
			ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).build();
		}
		
		Map<String, AttributeValue> idKey = new TreeMap<String, AttributeValue>();
		idKey.put("id", new AttributeValue(id));

		GetItemRequest request = new GetItemRequest()
		.withKey(idKey)
		.withTableName("variations_tokens");
			
		Map<String,AttributeValue> itemResult = ddb.getItem(request).getItem();
		if(itemResult != null) {
			@SuppressWarnings("unused")
			String token = itemResult.get("token").getS();
			
			long timestamp = Long.parseLong(itemResult.get("timestamp").getS());
			if(System.currentTimeMillis() - timestamp < 300000) {
				result = true;
			}
		}
		
		return result;
	}
}
