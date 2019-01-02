package com.dbm.variations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class Delete implements RequestStreamHandler, RequestHandler<Object, Object>  {

	public Delete() {}

	AmazonDynamoDB client;
	DynamoDB ddb;

	@Override
	public void handleRequest(InputStream input, OutputStream output,
			Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		String inputString = IOUtils.toString(input, "UTF-8");
		logger.log("inputString: " + inputString);
		EightcEvent event = new Gson().fromJson(inputString, EightcEvent.class);
		logger.log("event: " + event.toJson());

		if (client == null) {
			client = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).build();
			ddb = new DynamoDB(client);
		}
		boolean authorized = new Authorization(context).isAuthorized(event.getUid(), event.getToken()); // missing token
		if(authorized) { 
			logger.log(event.getUid() + " authenticated to delete " + event.getEventId());			
			Table eventsTable = ddb.getTable("variations_events");

			DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey("eventId", event.getEventId());
			eventsTable.deleteItem(deleteItemSpec);
		}
	}

	@Override
	public Object handleRequest(Object arg0, Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
