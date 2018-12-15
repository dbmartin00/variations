package com.dbm.variations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class QueryHandler implements RequestStreamHandler, RequestHandler<Object, Object>  {

    public QueryHandler() {}

	@Override
	public void handleRequest(InputStream input, OutputStream output,
			Context context) throws IOException {
		com.google.gson.Gson gson = new com.google.gson.Gson();
		Query query = gson.fromJson(IOUtils.toString(input, "UTF-8"), Query.class);
		String result = gson.toJson(findEightcEvents(query));
		PrintWriter writer = new PrintWriter(output);
		writer.println(result);
		writer.flush();
		writer.close();
	}

	private List<EightcEvent> findEightcEvents(Query query) {
		List<EightcEvent> results = new LinkedList<EightcEvent>();
		results.add(new EightcEvent("compassion", System.currentTimeMillis(), System.currentTimeMillis(), 3));
		return results;
	}

	@Override
	public Object handleRequest(Object arg0, Context arg1) {
		return null;
	}

}