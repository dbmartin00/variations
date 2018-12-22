package com.dbm.variations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Authenticate implements RequestStreamHandler, RequestHandler<Object, Object>  {

    public Authenticate() {}

	AmazonDynamoDB ddb;
	
	@Override
	public void handleRequest(InputStream input, OutputStream output,
			Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		logger.log("Input: " + input);
		
		Credential credential = new Gson().fromJson(IOUtils.toString(input, "UTF-8"), Credential.class);

		if (ddb == null) {
			ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).build();
		}
		JsonObject response = new JsonObject();
		response.addProperty("error", "username or password not found");
		
		Map<String, AttributeValue> addressKey = new TreeMap<String, AttributeValue>();
		addressKey.put("username", new AttributeValue(credential.getUsername()));

		GetItemRequest request = new GetItemRequest()
		.withKey(addressKey)
		.withTableName("variations_credentials");

		Map<String,AttributeValue> itemResult = ddb.getItem(request).getItem();
		if(itemResult != null) {
			String password = itemResult.get("password").getS();
			String id = itemResult.get("id").getN();
			if(password.equals(credential.getPassword())) {
				Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				AttributeValue tokenValue = new AttributeValue(UUID.randomUUID().toString());
				item.put("token", tokenValue);
				item.put("timestamp", new AttributeValue("" + System.currentTimeMillis()));
				AttributeValue idValue = new AttributeValue(id);
				item.put("id", idValue);

				ddb.putItem("variations_tokens", item);
				response.addProperty("error", "");	
				response.addProperty("message", "authentication successful");
				response.addProperty("token", tokenValue.getS());
				response.addProperty("id", idValue.getS());				
			} else {
				response.addProperty("error", "password doesn't match");				
			}
		} else {
			response.addProperty("error", "username not found");				
		}
		
		PrintWriter writer = new PrintWriter(output);
		writer.println(response.toString());
		writer.flush();
		writer.close();
	}
	
	@Override
	public Object handleRequest(Object arg0, Context arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
	