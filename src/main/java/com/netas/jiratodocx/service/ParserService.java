package com.netas.jiratodocx.service;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;
import com.netas.jiratodocx.dto.JiraUseCaseQuerySearchResultDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class ParserService {

	public static String jiraJsonStr;

	// Read Json file and convert to java
	public JiraUseCaseQuerySearchResultDTO readJSONforJira(String JiraProjectURL)
			throws JsonParseException, JsonMappingException, IOException {

		String auth = new String(
				Base64.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
		Client client = Client.create();
		WebResource webResource = client.resource(JiraProjectURL);

		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		String responses = response.getEntity(String.class);

		ObjectMapper mapper = new ObjectMapper();
		// Convert JSON string from file to Object
		return mapper.readValue(responses, JiraUseCaseQuerySearchResultDTO.class);

	}

	// Read images from jira
	public ArrayList<String> parseJSON(String urlString) throws IOException {

		String auth = new String(
				Base64.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
		Client client = Client.create();
		WebResource webResource = client.resource(urlString);

		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		String responses = response.getEntity(String.class);

		jiraJsonStr = responses;

		JSONObject json;
		ArrayList<String> images = new ArrayList<String>();

		json = new JSONObject(jiraJsonStr);
		JSONObject fields = json.getJSONObject("fields");
		JSONArray attachments = fields.getJSONArray("attachment");
		for (int i = 0; i < attachments.length(); i++) {
			JSONObject object = (JSONObject) attachments.getJSONObject(i);
			if (object.getString("content").endsWith(".png") || object.getString("content").endsWith(".jpg")
					|| object.getString("content").endsWith(".PNG") || object.getString("content").endsWith(".JPG"))
				images.add(object.getString("content"));
		}

		return images;

	}

	// Parsing Kullanılan KS'ler from Jira API
	public String parseUsageScenarios(String usageScenarios) throws IOException {

		String[] ks = usageScenarios.split(", ");
		String output = "";

		for (int i = 0; i < ks.length; i++) {

			String url = "http://link/rest/api/2/issue/" + ks[i];

			String auth = new String(Base64
					.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
			Client client = Client.create();
			WebResource webResource = client.resource(url);

			ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
					.accept("application/json").get(ClientResponse.class);
			String responses = response.getEntity(String.class);

			JSONObject json;

			json = new JSONObject(responses);

			JSONObject fields = json.getJSONObject("fields");
			String summary = (String) fields.get("summary");
			output += (i + 1) + ". " + summary + "\n";

		}

		return output;
	}

	// Parsing Kullanılan KS'ler from Jira API
	public String parseRequirementInfo(String usageScenarios) {

		String[] ks = usageScenarios.split(", ");
		String output = "";

		for (int i = 0; i < ks.length; i++) {

			String url = "http://link/rest/api/2/issue/" + ks[i];

			String auth = new String(Base64
					.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
			Client client = Client.create();
			WebResource webResource = client.resource(url);

			ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
					.accept("application/json").get(ClientResponse.class);
			String responses = response.getEntity(String.class);

			JSONObject json = null;

			if (responses != null && responses.startsWith("{")) {
				json = new JSONObject(responses);
				JSONObject fields = json.getJSONObject("fields");
				String summary = (String) fields.get("customfield_10201");
				output += summary;
			}

		}

		return output;
	}

}
