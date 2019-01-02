package com.dbm.variations;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class EmailRegistrationHandler implements
RequestHandler<EmailRegistration, Object> {

	AmazonDynamoDB ddb;

	@Override
	public Object handleRequest(EmailRegistration input, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("Input: " + input);
		if (ddb == null) {
			ddb = initDynamoDbClient();
		}
		JSONObject response = new JSONObject();

		Map<String, AttributeValue> usernameKey = new TreeMap<String, AttributeValue>();
		usernameKey.put("username", new AttributeValue(input.getUsername()));

		GetItemRequest request = new GetItemRequest()
		.withKey(usernameKey)
		.withTableName("variations_credentials");

		Map<String,AttributeValue> itemResult = ddb.getItem(request).getItem();
		if(itemResult != null) {
			logger.log("dynamo returned: " + itemResult);
			String id = itemResult.get("id").getS();
			logger.log("id : '" + id + "' input.getId(): '" + input.getId() + "'");
			if(!input.getId().equals(id)) {
				// not a valid id; send a new one
				response.put("message", "not a valid id, '" + input.getId() + "'; check your mail for a valid registration link: " + input.getUsername());
			} else {
				Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("id", new AttributeValue(id));
				item.put("username", new AttributeValue(input.getUsername()));
				AttributeValue trueValue = new AttributeValue();
				trueValue.setBOOL(Boolean.TRUE);
				item.put("valid", trueValue);
				item.put("password", new AttributeValue(itemResult.get("password").getS()));
				ddb.putItem("variations_credentials", item);

				response.put("message", "address validated");
			}
		} else {
			String uuid = UUID.randomUUID().toString();
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("id", new AttributeValue(uuid));
			item.put("username", new AttributeValue(input.getUsername()));
			AttributeValue trueValue = new AttributeValue();
			trueValue.setBOOL(Boolean.FALSE);
			item.put("valid", trueValue);

			ddb.putItem("variations_credentials", item);

			String result = "";
			try {
				result = sendRegistrationEmail(uuid, input.getUsername());
				response.put("message", result);
			} catch (Exception e) {
				response.put("error", "problem sending initial registration email: " + e.getMessage());			
			}
		}

		return response.toString();
	}

	private String sendRegistrationEmail(String id, String address) throws Exception {
		String result = "error during registration email send to address (" + address + ")";

		// Create a Properties object to contain connection configuration information.
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", PORT); 
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");

		// Create a Session object to represent a mail session with the specified properties. 
		Session session = Session.getDefaultInstance(props);

		// Create a message with the specified information. 
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(FROM, FROMNAME));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
		msg.setSubject(SUBJECT);

		String BODY = String.join(
				System.getProperty("line.separator"),
				"<h1><a href=\"@@URL@@?id=@@ID@@&address=@@ADDRESS@@\">Click on this link to complete your registration.</a></h1>");

		BODY = BODY.replaceAll("@@ID@@", id);
		BODY = BODY.replaceAll("@@ADDRESS@@", address);
		BODY = BODY.replaceAll("@@URL@@", "https://1oaodu1n44.execute-api.us-west-2.amazonaws.com/prod");
		msg.setContent(BODY, "text/html");

		// Create a transport.
		Transport transport = session.getTransport();

		// Send the message.
		try {

			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			result = "registration email sent";

		} finally {
			transport.close();
		}

		return result;
	}

	private AmazonDynamoDB initDynamoDbClient() {
		return AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.US_WEST_2).build();
	}


	// Replace sender@example.com with your "From" address.
	// This address must be verified.
	static final String FROM = "david.martin@gmail.com";
	static final String FROMNAME = "David Brooke Martin";

	// Replace recipient@example.com with a "To" address. If your account 
	// is still in the sandbox, this address must be verified.
	static final String TO = "recipient@example.com";

	// Replace smtp_username with your Amazon SES SMTP user name.
	static final String SMTP_USERNAME = "FOO";

	// Replace smtp_password with your Amazon SES SMTP password.
	static final String SMTP_PASSWORD = "BAR";

	// Amazon SES SMTP host name. This example uses the US West (Oregon) region.
	// See http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html#region-endpoints
	// for more information.
	static final String HOST = "email-smtp.us-west-2.amazonaws.com";

	// The port you will connect to on the Amazon SES SMTP endpoint. 
	static final int PORT = 587;

	static final String SUBJECT = "Validate your email for Cortazar";

}
