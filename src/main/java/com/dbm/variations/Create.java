package com.dbm.variations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class Create implements RequestStreamHandler, RequestHandler<Object, Object>  {

	public Create() {}

	AmazonDynamoDB ddb;

	@Override
	public void handleRequest(InputStream input, OutputStream output,
			Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		String inputString = IOUtils.toString(input, "UTF-8");
		logger.log("inputString: " + inputString);
		EightcEvent event = new Gson().fromJson(inputString, EightcEvent.class);
		logger.log("event: " + event.toJson());

		if (ddb == null) {
			ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).build();
		}
		boolean authorized = new Authorization(context).isAuthorized(event.getUid(), event.getToken()); // missing token
		if(authorized) { 
			logger.log(event.getUid() + " authenticated");
			String uuid = UUID.randomUUID().toString();
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("eventId", new AttributeValue(uuid));
			item.put("eightc", new AttributeValue(event.getEightc()));
			item.put("endDate", new AttributeValue("" + event.getEndDate()));
			item.put("startDate", new AttributeValue("" + event.getStartDate()));
			item.put("happening", new AttributeValue("" + event.getHappening()));
			item.put("valence", new AttributeValue("" + event.getValence()));
			ddb.putItem("variations_events", item);

			Map<String, AttributeValue> idKey = new HashMap<String, AttributeValue>();
			idKey.put("uid", new AttributeValue().withS(event.getUid()));
//			String updateExpression = "SET #events = list_append(#events, :eventId)";
			String updateExpression = "ADD #events :eventId";
			
			Map<String, String> expressionAttributeNames = new HashMap<String, String>();
			expressionAttributeNames.put("#events", "events");
			
			Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
			expressionAttributeValues.put(":eventId", new AttributeValue().withSS(uuid));
			
			UpdateItemRequest updateItemRequest = new UpdateItemRequest()
			.withTableName("variations_id2events")
			.withKey(idKey)
			.withUpdateExpression(updateExpression)
			.withExpressionAttributeNames(expressionAttributeNames)
			.withExpressionAttributeValues(expressionAttributeValues);
			
			UpdateItemResult updateItem = ddb.updateItem(updateItemRequest);
			logger.log("update result: " + updateItem.getAttributes());
		}
	}

	@Override
	public Object handleRequest(Object arg0, Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
