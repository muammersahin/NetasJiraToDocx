package com.netas.jiratodocx.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class CreateDiagramFromJira {

	public static ArrayList<String> imageNames = new ArrayList<>();
	public static ArrayList<String> otherImageNames = new ArrayList<>();

	public static void addDiagramImagesToDocument() throws IOException {

		if (!JiraStaticFileURLAddressesDTO.diagramCode.equals("yok")) {

			ParserService parser = new ParserService();
			ArrayList<String> imageUrls = parser.parseJSON(JiraStaticFileURLAddressesDTO.diagramURL);

			String auth = new String(Base64
					.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
			Client client = Client.create();

			for (int i = 0; i < imageUrls.size(); i++) {

				WebResource webResource = client.resource(imageUrls.get(i));

				ClientResponse response = webResource.header("Authorization", "Basic " + auth)
						.get(ClientResponse.class);
				BufferedImage responses = response.getEntity(BufferedImage.class);

				int startIndex = imageUrls.get(i).lastIndexOf("/");
				String imageName = imageUrls.get(i).substring(startIndex + 1, imageUrls.get(i).length());

				String newImageName = "images/" + "usecase-diyagram" + i + ".png";
				File outputfile = new File(newImageName);
				imageNames.add(imageName);
				ImageIO.write(responses, "png", outputfile);

			}
		}
	}

	public static void addOtherDiagramImagesToDocument() throws IOException {

		if (!JiraStaticFileURLAddressesDTO.otherDiagramCode.equals("yok")) {

			ParserService parser = new ParserService();
			ArrayList<String> imageUrls = parser.parseJSON(JiraStaticFileURLAddressesDTO.otherDiagramURL);

			String auth = new String(Base64
					.encode(JiraStaticFileURLAddressesDTO.username + ":" + JiraStaticFileURLAddressesDTO.password));
			Client client = Client.create();

			for (int i = 0; i < imageUrls.size(); i++) {

				WebResource webResource = client.resource(imageUrls.get(i));

				ClientResponse response = webResource.header("Authorization", "Basic " + auth)
						.get(ClientResponse.class);
				BufferedImage responses = response.getEntity(BufferedImage.class);

				int startIndex = imageUrls.get(i).lastIndexOf("/");
				String imageName = imageUrls.get(i).substring(startIndex + 1, imageUrls.get(i).length());

				String newImageName = "images/" + "other-diyagram" + i + ".png";
				File outputfile = new File(newImageName);
				otherImageNames.add(imageName);
				ImageIO.write(responses, "png", outputfile);

			}
		}
	}

}
