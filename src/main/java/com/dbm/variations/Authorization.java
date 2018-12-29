package com.dbm.variations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Authorization {

	private static final int kMaxTokenAgeInMillis = 3600000;
	AmazonDynamoDB ddb;
	private LambdaLogger logger;
	SimpleDateFormat formatter;
	
	public Authorization(Context context) {
		this.logger = context.getLogger();
		this.formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}	
	
	public boolean
	isAuthorized(String id, String token) {
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
		logger.log("is id '" + id + "' authorized?");
		if(itemResult != null) {
			String validToken = itemResult.get("token").getS();
			
			logger.log("does input token match token stored withs id? " + token + " ?= " + validToken);
			if(token.equalsIgnoreCase(validToken)) { 
				long timestamp = Long.parseLong(itemResult.get("timestamp").getS());
				logger.log("is token timestamp within " + kMaxTokenAgeInMillis + " of " + formatter.format(new Date(timestamp)));
				if(System.currentTimeMillis() - timestamp < kMaxTokenAgeInMillis) {
					logger.log("valid token; authenticated");
					result = true;					
				} else {
					logger.log("token expired");
				}
			} else {
				logger.log("no match on token provided");
			}
		}
		
		return result;
	}
}
