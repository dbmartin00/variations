package com.dbm.variations;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class EmailValidationHandler implements RequestHandler<IdAddress, Object> {

	AmazonDynamoDB ddb;

	@Override
	public Object handleRequest(IdAddress input, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("Input: " + input.toString());
		JSONObject result = new JSONObject();
		
		if (ddb == null) {
			ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).build();
		}
		
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("id", new AttributeValue(input.getId()));
		item.put("address", new AttributeValue(input.getUsername()));
	    AttributeValue trueAttributeValue = new AttributeValue();
	    trueAttributeValue.setBOOL(Boolean.TRUE);
		item.put("valid", trueAttributeValue);
		
		logger.log("id: " + input.getId());
		logger.log("address: " + input.getUsername());
		logger.log("valid: true");
		
		ddb.putItem("fiction_readers", item);
		
		result.put("message", "address and uuid validated successfully");
		return result.toString();
	}

}
