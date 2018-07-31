package com.netas.jiratodocx.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;
import com.netas.jiratodocx.dto.JiraUseCaseQuerySearchResultDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class DocxConverterService {

	Logger log = Logger.getLogger(DocxConverterService.class);
	public static ArrayList<String> jiraNames = new ArrayList<String>();
	public static ArrayList<Integer> imageSizeofJiraDocx = new ArrayList<Integer>();
	public static ArrayList<ArrayList<String>> imgURLArray = new ArrayList<ArrayList<String>>();

	// Write all the informations to Docx file
	public void writeToDocx(JiraUseCaseQuerySearchResultDTO jiraUseCaseQuerySearchResult) throws Exception {

		// Map for replacing variables
		Map<String, String> mapping = new HashMap<String, String>();
		String output = "";
		ParserService parser = new ParserService();

		// Create a String which includes Fonts, indents, line spacing and stype
		// information for Docx XML file

		log.info("Parsing Jira Rest API");
		boolean getKScode = false;

		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> hashMapForKSCode = (LinkedHashMap<String, Object>) jiraUseCaseQuerySearchResult
				.getIssues().get(0);
		LinkedHashMap<String, Object> KsCodeParsing = hashMapForKSCode;
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> KsCodesecondParsing = (LinkedHashMap<String, Object>) KsCodeParsing.get("fields");

		if (getKScode == false) {
			String codeSyntax = (String) KsCodesecondParsing.get("summary");

			ArrayList<?> descParsing = (ArrayList<?>) KsCodesecondParsing.get("components");
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> descHashParsing = (LinkedHashMap<String, Object>) descParsing.get(0);

			String description = (String) descHashParsing.get("description");
			JiraStaticFileURLAddressesDTO.description = description;
			int firstLine = codeSyntax.indexOf("_");
			String KScode = codeSyntax.substring(0, firstLine);
			JiraStaticFileURLAddressesDTO.KScode = KScode;
			getKScode = true;
		}

		// Docx4j
		FillTemplateDocx.initialize();

		// Try for all issues
		for (int j = 0; j < jiraUseCaseQuerySearchResult.getIssues().size(); j++) {

			String temp = "";

			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) jiraUseCaseQuerySearchResult
					.getIssues().get(j);
			LinkedHashMap<String, Object> firstParsing = linkedHashMap;
			// Parse to avoid unnecessary information
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> secondParsing = (LinkedHashMap<String, Object>) firstParsing.get("fields");

			// Insertion of Values
			log.info("Parsing " + secondParsing.get("summary"));

			// customfield_10205 = Akış Bilgileri
			output = "" + secondParsing.get("customfield_10205");
			// Replace < to avoid error
			output = output.replaceAll("<", "&lt;");

			// Check if it is null or not
			if (output.contains("null")) {
				output = "Yok";
			} else {

				output = CorrectionAndBeautifyingStringObject.string_corrector(output);

				// insert </w:t><w:br/><w:t> for next line
				output = output.replaceAll("\n", "</w:t><w:br/><w:t>");

				// Delete last next line
				int lastIndex = output.lastIndexOf("</w:t><w:br/><w:t>");
				if (lastIndex != -1) {
					temp = output;
					output = output.substring(0, lastIndex);
				}
				if (!temp.endsWith("</w:t><w:br/><w:t>")) {
					output = temp;
				}

			}
			// Add to map to replace with template file
			mapping.put("akis", output);

			log.info("Akış is parsed!");

			// customfield_10204 = Kullanılan KS'ler
			output = "" + secondParsing.get("customfield_10204");
			// delete some unnecessary characters to avoid errors and simplify Docx File
			output = output.replaceAll("<", "&lt;");
			output = output.replaceAll("\\[", "");
			output = output.replaceAll("\\]", "");
			if (output.contains("null")) {
				output = "Yok";
			} else {
				// Parse Kullanılan KS'ler
				output = parser.parseUsageScenarios(output);

				// Insert new line
				output = output.replaceAll("\n", "</w:t><w:br/><w:t>");
				output = output.replaceAll("-", ":");
				int lastIndex = output.lastIndexOf("</w:t><w:br/><w:t>");
				if (lastIndex != -1) {
					temp = output;
					output = output.substring(0, lastIndex);
				}
				if (!temp.endsWith("</w:t><w:br/><w:t>")) {
					output = temp;
				}

			}
			mapping.put("kks", output);
			log.info("Kulanılan KS'ler is parsed!");

			// customfield_10206 = Senaryolar
			output = "" + secondParsing.get("customfield_10206");
			output = output.replaceAll("<", "&lt;");
			output = output.replaceAll("\n", "</w:t><w:br/><w:t>");

			if (output.contains("null")) {
				output = "Yok";
			} else {

				int lastIndex = output.lastIndexOf("</w:t><w:br/><w:t>");
				if (lastIndex != -1) {
					temp = output;
					output = output.substring(0, lastIndex);
				}
				if (!temp.endsWith("</w:t><w:br/><w:t>")) {
					output = temp;
				}

			}
			mapping.put("senaryolar", output);
			log.info("Senaryolar is parsed!");

			// customfield_10207 = İş Kuralları
			output = "" + secondParsing.get("customfield_10207");
			output = output.replaceAll("<", "&lt;");
			output = output.replaceAll("\n", "</w:t><w:br/><w:t>");
			if (output.contains("null")) {
				output = "Yok";
			} else {
				int lastIndex = output.lastIndexOf("</w:t><w:br/><w:t>");
				if (lastIndex != -1) {
					temp = output;
					output = output.substring(0, lastIndex);
					if (!temp.endsWith("</w:t><w:br/><w:t>")) {
						output = temp;
					}
				}

			}
			mapping.put("kural", output);
			log.info("İş kuralları is parsed!");

			// summary = Bilgi - Başlık
			output = "" + secondParsing.get("summary");
			output = output.replaceAll("<", "");
			if (output.lastIndexOf("-") != -1) {
				output = output.substring(output.lastIndexOf("-") + 1, output.length());
			} else {
				output = output.substring(output.indexOf(" ") + 1, output.length());
			}
			if (output.contains("null")) {
				output = "Yok";
			}
			output = output.replaceAll("\n", "</w:t><w:br/><w:t>");
			// add the name of file to Arraylist
			log.info("Summary is parsed!");

			mapping.put("bilgi", output);

			// customfield_10201 = Kullanım Senaryosu
			output = "" + secondParsing.get("customfield_10201");
			output = output.replaceAll("<", "&lt;");
			output = output.replaceAll("\n", "</w:t><w:br/><w:t>");
			if (output.contains("null")) {
				output = "Yok";
			}
			// add Kullanım Senaryo numaraları to store and use later for images
			jiraNames.add(output);

			JiraStaticFileURLAddressesDTO.ksNo = output;

			mapping.put("numara", output);
			log.info("Kullanım Senaryo Numarası is parsed!");

			output = "" + secondParsing.get("customfield_10202");
			output = output.replaceAll("<", "&lt;");
			output = output.replaceAll("\n", "</w:t><w:br/><w:t>");
			if (output.contains("null")) {
				output = "Yok";
			} else {

				int lastIndex = output.lastIndexOf("</w:t><w:br/><w:t>");
				if (lastIndex != -1) {
					output = output.substring(0, lastIndex);
				}

			}
			mapping.put("aktor", output);
			log.info("Aktör is parsed!");

			output = "" + GetRmsisLinksFromJira.getRmsisLinkFromJira(firstParsing.get("id"));
			output = output.replaceAll("<", "&lt;");
			output = output.replaceAll("\n", "</w:t><w:br/><w:t>");
			String[] numberedReq = output.split("</w:t><w:br/><w:t>");
			output = "";
			for (int i = 0; i < numberedReq.length; i++) {
				output += (i + 1) + ". " + numberedReq[i] + "</w:t><w:br/><w:t>";
			}

			if (output.contains("null") || output.equals("")) {
				output = "Yok";
			} else {
				int lastIndex = output.lastIndexOf("</w:t><w:br/><w:t>");
				if (lastIndex != -1) {
					output = output.substring(0, lastIndex);
				}
			}
			mapping.put("gereksinim", output);
			log.info("Gereksinimler is parsed!");

			// Getting Image links
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, String> thirdParsing = (LinkedHashMap<String, String>) secondParsing.get("watches");
			String urlForJiraIssues = thirdParsing.get("self");
			urlForJiraIssues = urlForJiraIssues.replaceAll("watchers", "");

			ArrayList<String> imageUrls = parser.parseJSON(urlForJiraIssues);

			String auth = new String(Base64
					.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
			Client client = Client.create();
			imageSizeofJiraDocx.add(imageUrls.size());
			ArrayList<String> imageNames = new ArrayList<String>();
			// Download images
			for (int i = 0; i < imageUrls.size(); i++) {

				WebResource webResource = client.resource(imageUrls.get(i));

				ClientResponse response = webResource.header("Authorization", "Basic " + auth)
						.get(ClientResponse.class);
				BufferedImage responses = response.getEntity(BufferedImage.class);

				int startIndex = imageUrls.get(i).lastIndexOf("/");
				String imageName = imageUrls.get(i).substring(startIndex + 1, imageUrls.get(i).length());

				if (!(imageName.startsWith("AC") || imageName.startsWith("MC")))
					log.info(
							"There is an image which is attached wrongly. Please use AC-MC before image names. Summary: "
									+ secondParsing.get("summary"));
				if (imageName.startsWith("AC") || imageName.startsWith("ac")) {
					String newImageName = "images/" + secondParsing.get("customfield_10201") + "-diyagram" + ".png";
					File outputfile = new File(newImageName);
					imageNames.add(newImageName);
					ImageIO.write(responses, "png", outputfile);
				} else if (imageName.startsWith("MC") || imageName.startsWith("mc")) {
					String newImageName = "images/" + secondParsing.get("customfield_10201") + "-arayuz" + ".png";
					File outputfile = new File(newImageName);
					imageNames.add(newImageName);
					ImageIO.write(responses, "png", outputfile);
				}

			}

			// Add image names to an ArrayList
			imgURLArray.add(imageNames);

			log.info("Neccessary Images have downloaded!");

			log.info("Template File is being filled...");
			// Call the method that changes template's variables
			FillTemplateDocx.replaceVariablesInTemplate(mapping, "" + secondParsing.get("summary"), imageUrls);

		}

		FillTemplateDocx.createDocxFile();
		AddImageToDocxDocuments.addImage(jiraNames, imageSizeofJiraDocx, imgURLArray);

	}

}
