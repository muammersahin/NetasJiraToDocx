package com.netas.jiratodocx.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class GetRmsisLinksFromJira {

	// Parsing Rmsis Links to get Issue Names of Them
	public static String getRmsisLinkFromJira(Object object) {

		String auth = new String(com.sun.jersey.core.util.Base64
				.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
		Client client = Client.create();
		WebResource webResource = client.resource("http://link/secure/viewRequirements.jspa?id=" + object);

		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		String responses = response.getEntity(String.class);

		// Regex for Issue Names
		Pattern p = Pattern.compile("class=\\\"rmsis-link\\\" target=\\\"_blank\\\" href=\\\"(.*?)\\\">(.*?)<\\/a>");
		Matcher m = p.matcher(responses);
		String allRmsisNames = "";
		String name = null;
		while (m.find()) {

			name = m.group(2);
			name = name.replaceAll("\"", "");
			try {
				allRmsisNames += getRequirementCodeViaParsingCSV(name).replaceAll("\"", "") + "\n";
			} catch (Exception e) {
			}
			if (!JiraStaticFileURLAddressesDTO.rmsisMatris.containsKey(getRequirementCodeViaParsingCSV(name))) {
				JiraStaticFileURLAddressesDTO.rmsisMatris.put(getRequirementCodeViaParsingCSV(name),
						JiraStaticFileURLAddressesDTO.ksNo);
			} else {
				String oldText = JiraStaticFileURLAddressesDTO.rmsisMatris.get(getRequirementCodeViaParsingCSV(name));
				oldText += ", " + JiraStaticFileURLAddressesDTO.ksNo;
				JiraStaticFileURLAddressesDTO.rmsisMatris.put(getRequirementCodeViaParsingCSV(name), oldText);
			}

		}

		return allRmsisNames;

	}

	public static String getRequirementCodeViaParsingCSV(String requirementId) {

		String csvFile = "requirements.csv";
		String line = "";
		String requirementCode = null;

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {

				String[] splitCsv = line.split(";");
				// use comma as separator

				if (splitCsv[0].equals("\"" + requirementId + "\""))
					requirementCode = splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", "");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return requirementCode;
	}

}
