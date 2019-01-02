package com.dbm.variations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

public class QueryHandler implements RequestStreamHandler, RequestHandler<Object, Object>  {

    public QueryHandler() {
		
		if (ddb == null) {
			ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).build();
		}
		
    }
	
    AmazonDynamoDB ddb;
	
	@Override
	public void handleRequest(InputStream input, OutputStream output,
			Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		logger.log("Input: " + input);
		if (ddb == null) {
			ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).build();
		}
		
		Gson gson = new com.google.gson.Gson();
		Query query = gson.fromJson(IOUtils.toString(input, "UTF-8"), Query.class);
		
		String result = "{ \"error\" : \"unauthorized token\"}";
		boolean authorized = new Authorization(context).isAuthorized(query.getUid(), query.getToken());
		if(authorized) {
			logger.log("user: " + query.getUid() + " authenticated");
			
			Map<String, AttributeValue> idKey = new TreeMap<String, AttributeValue>();
			idKey.put("uid", new AttributeValue(query.getUid()));

			GetItemRequest request = new GetItemRequest()
			.withKey(idKey)
			.withTableName("variations_id2events");
			
			Map<String,AttributeValue> itemResult = ddb.getItem(request).getItem();
			if(itemResult != null) {
				List<String> events = itemResult.get("events").getSS();
				List<EightcEvent> eightcJson = new LinkedList<EightcEvent>();
				for(String eventId : events) {
					Map<String, AttributeValue> eventIdKey = new TreeMap<String, AttributeValue>();
					eventIdKey.put("eventId", new AttributeValue(eventId));

					GetItemRequest eventRequest = new GetItemRequest()
					.withKey(eventIdKey)
					.withTableName("variations_events");
					
					Map<String, AttributeValue> eventResult = ddb.getItem(eventRequest).getItem();
					if(eventResult != null) {
						String eightc = eventResult.get("eightc").getS();
						String endDate = eventResult.get("endDate").getS();
						String happening = eventResult.get("happening").getS();
						String startDate = eventResult.get("startDate").getS();
						String valence = eventResult.get("valence").getS();
						EightcEvent e = new EightcEvent(query.getUid(), eightc, Long.parseLong(startDate), Long.parseLong(endDate), Integer.parseInt(valence), happening, "", eventId);
						logger.log(e.toJson());
						eightcJson.add(e);
					}					
				}
				String newLine = System.getProperty("line.separator");
				result = "[ " + newLine;
				for(EightcEvent event : eightcJson) {
					result += event.toJson() + "," + newLine;
				}
				if(result.indexOf(",") != -1) {
					result = result.substring(0, result.lastIndexOf(","));
				}
				result += " ]" + newLine;
			}
		}
		
		PrintWriter writer = new PrintWriter(output);
		writer.println(result);
		writer.flush();
		writer.close();
	}

	@Override
	public Object handleRequest(Object arg0, Context arg1) {
		return null;
	}

}