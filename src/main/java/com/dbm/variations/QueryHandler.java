package com.dbm.variations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
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
		
		String result = "{ \"error\" : \"token not found\"}";
		
		Gson gson = new com.google.gson.Gson();
		Query query = gson.fromJson(IOUtils.toString(input, "UTF-8"), Query.class);

		boolean authorized = new Authorization().isAuthorized(query.getId());
		if(authorized) {
			//
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