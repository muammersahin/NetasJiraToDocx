/*
 *  Copyright 2007-2008, Plutext Pty Ltd.
 *   
 *  This file is part of docx4j.
    docx4j is licensed under the Apache License, Version 2.0 (the "License"); 
    you may not use this file except in compliance with the License. 
    You may obtain a copy of the License at 
        http://www.apache.org/licenses/LICENSE-2.0 
    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, 
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
    See the License for the specific language governing permissions and 
    limitations under the License.
 */

package com.netas.jiratodocx.service;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.Color;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;

public class AddImageToDocxDocuments {

	public static int sizeOfWordDocument;
	public static StyleDefinitionsPart sdp;
	public static StyleDefinitionsPart ndp;
	public static NumberingDefinitionsPart dsp;

	public static Logger log = Logger.getLogger(AddImageToDocxDocuments.class);

	// Method to add image to a particular position and create byte Array
	public static byte[] addingImageToParticularPosition(String imgURL) throws Exception {

		// The image to add
		File file = new File(imgURL);

		// Our utility method wants that as a byte array
		java.io.InputStream is = new java.io.FileInputStream(file);
		long length = file.length();
		// You cannot create an array using a long type.
		// It needs to be an int type.
		if (length > Integer.MAX_VALUE) {
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		is.close();

		return bytes;

	}

	// Create image with width declaration
	public static org.docx4j.wml.P newImageWithWidth(WordprocessingMLPackage wordMLPackage, byte[] bytes,
			String filenameHint, String altText, int id1, int id2, long cx) throws Exception {

		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

		Inline inline = imagePart.createImageInline(filenameHint, altText, id1, id2, cx, false);

		// Now add the inline in w:p/w:r/w:drawing
		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		org.docx4j.wml.P p = factory.createP();
		org.docx4j.wml.R run = factory.createR();
		p.getContent().add(run);
		org.docx4j.wml.Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);

		return p;

	}

	// Create image without any declaration
	public static org.docx4j.wml.P newImage(WordprocessingMLPackage wordMLPackage, byte[] bytes, String filenameHint,
			String altText, int id1, int id2) throws Exception {

		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

		Inline inline = imagePart.createImageInline(filenameHint, altText, id1, id2, false);

		// Now add the inline in w:p/w:r/w:drawing
		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		org.docx4j.wml.P p = factory.createP();
		org.docx4j.wml.R run = factory.createR();
		p.getContent().add(run);
		org.docx4j.wml.Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);

		return p;

	}

	// Add image to docx file
	@SuppressWarnings("deprecation")
	public static void addImage(ArrayList<String> jiraNames, ArrayList<Integer> imageSizeofJiraDocx,
			ArrayList<ArrayList<String>> imgURLArray) throws Exception {

		WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new File("temp.docx"));
		MainDocumentPart documentPart = wordPackage.getMainDocumentPart();

		for (int i = 0; i < jiraNames.size(); i++) {
			if (2 * i + 1 < jiraNames.size() * 2) {

				for (int k = 0; k < imgURLArray.get(i).size(); k++) {

					// Check if it is diagram or not
					if (imgURLArray.get(i).get(k).endsWith("diyagram.png")) {

						String imgURL = "images/" + jiraNames.get(i) + "-diyagram" + ".png";

						P p = AddImageToDocxDocuments.newImageWithWidth(wordPackage,
								AddImageToDocxDocuments.addingImageToParticularPosition(imgURL), null, null, 0, 1,
								2000);

						// Add images to specified position
						@SuppressWarnings("unchecked")
						JAXBElement<Tbl> object = (JAXBElement<Tbl>) documentPart.getContent().get(2 * i + 1);
						Tbl table = (Tbl) object.getValue();
						Tr tableRow = (Tr) table.getContent().get(3);
						@SuppressWarnings("unchecked")
						JAXBElement<Tc> object2 = (JAXBElement<Tc>) tableRow.getContent().get(1);
						Tc tableColumn = (Tc) object2.getValue();
						tableColumn.getContent().set(0, p);

						object2.setValue(tableColumn);
						tableRow.getContent().set(1, object2);
						table.getContent().set(3, tableRow);
						object.setValue(table);
						documentPart.getContent().set(2 * i + 1, object);

					} else {
						String imgURL = "images/" + jiraNames.get(i) + "-arayuz" + ".png";

						P p = AddImageToDocxDocuments.newImageWithWidth(wordPackage,
								AddImageToDocxDocuments.addingImageToParticularPosition(imgURL), null, null, 0, 1,
								7500);

						@SuppressWarnings("unchecked")
						JAXBElement<Tbl> object = (JAXBElement<Tbl>) documentPart.getContent().get(2 * i + 1);
						Tbl table = (Tbl) object.getValue();
						Tr tableRow = (Tr) table.getContent().get(7);
						@SuppressWarnings("unchecked")
						JAXBElement<Tc> object2 = (JAXBElement<Tc>) tableRow.getContent().get(1);
						Tc tableColumn = (Tc) object2.getValue();
						tableColumn.getContent().set(0, p);

						object2.setValue(tableColumn);
						tableRow.getContent().set(1, object2);
						table.getContent().set(7, tableRow);
						object.setValue(table);
						documentPart.getContent().set((2 * i + 1), object);
						sizeOfWordDocument = 2 * i + 1;

					}

				}

			}

		}

		log.info("Images, Matris, Functions.. are being added. It will take a few minutes");

		wordPackage.getMainDocumentPart().addObject(CreateHeadingsWithStyle.get_title_object_with_style("Heading1",
				"Detaylı Analizde Ortaya Çıkarılan Fonksiyonel Gereksinimler", 32));
		wordPackage.getMainDocumentPart()
				.addObject(CreateFunctionalRequirements.createTable().getMainDocumentPart().getContent().get(0));

		wordPackage.getMainDocumentPart().getContent().add(0, CreateHeadingsWithStyle
				.get_title_object_with_style("Heading1", "Gereksinim Kullanım Senaryosu Matrisi", 32));

		wordPackage.getMainDocumentPart().getContent().add(1, CreateHeadingsWithStyle.get_title_object_with_style(
				"Heading1", JiraStaticFileURLAddressesDTO.description + " Paydaş Gereksinimleri", 32));

		List<Object> contentsOfTSM = CreateTSMDescriptions.getTSMDescriptions().getContent();

		for (int i = 0; i < contentsOfTSM.size(); i++) {
			wordPackage.getMainDocumentPart().getContent().add(i + 2, contentsOfTSM.get(i));
		}

		wordPackage.getMainDocumentPart().getContent().add(contentsOfTSM.size() + 2,
				CreateHeadingsWithStyle.get_title_object_with_style("Heading1", "Kullanım Senaryoları", 32));
		P ksenaryolari = (P) wordPackage.getMainDocumentPart().getContent().get(contentsOfTSM.size() + 2);
		wordPackage.getMainDocumentPart().getContent().set(contentsOfTSM.size() + 2, ksenaryolari);

		// Remove dublicates

		// wordPackage.getMainDocumentPart().addObject(
		// CreateFunctionalRequirements.getRequirementMatrix().getMainDocumentPart().getContent().get(0));

		wordPackage.getMainDocumentPart().getContent().add(1,
				CreateFunctionalRequirements.getRequirementMatrix().getMainDocumentPart().getContent().get(0));
		if (!(JiraStaticFileURLAddressesDTO.diagramCode.equals("yok")
				&& JiraStaticFileURLAddressesDTO.otherDiagramCode.equals("yok"))) {

			wordPackage.getMainDocumentPart()
					.addObject(CreateHeadingsWithStyle.get_title_object_with_style("Heading1", "Diyagramlar", 32));

		}

		CreateDiagramFromJira.addDiagramImagesToDocument();
		CreateDiagramFromJira.addOtherDiagramImagesToDocument();

		ArrayList<String> imageNames = CreateDiagramFromJira.imageNames;
		ArrayList<String> otherImageNames = CreateDiagramFromJira.otherImageNames;

		if (!JiraStaticFileURLAddressesDTO.otherDiagramCode.equals("yok")) {

			for (int i = 0; i < otherImageNames.size(); i++) {

				String imgURL = "images/" + "other-diyagram" + i + ".png";

				P paragraph = AddImageToDocxDocuments.newImageWithWidth(wordPackage,
						AddImageToDocxDocuments.addingImageToParticularPosition(imgURL), null, null, 0, 1, 7000);

				String title = otherImageNames.get(i).replaceAll(".png", "");
				title = TurkishCharacterFixService.getFixedHeadings(title);

				wordPackage.getMainDocumentPart()
						.addObject(CreateHeadingsWithStyle.get_title_object_with_style("Heading2", title, 26));
				wordPackage.getMainDocumentPart().addObject(paragraph);

			}

		}

		if (!JiraStaticFileURLAddressesDTO.diagramCode.equals("yok")) {

			wordPackage.getMainDocumentPart().addObject(CreateHeadingsWithStyle.get_title_object_with_style("Heading2",
					"Kullanım Senaryosu Diagramları", 26));
			for (int i = 0; i < imageNames.size(); i++) {

				String imgURL = "images/" + "usecase-diyagram" + i + ".png";

				P paragraph = AddImageToDocxDocuments.newImageWithWidth(wordPackage,
						AddImageToDocxDocuments.addingImageToParticularPosition(imgURL), null, null, 0, 1, 7000);

				String title = imageNames.get(i).replaceAll(".png", "");
				title = TurkishCharacterFixService.getFixedHeadings(title);

				wordPackage.getMainDocumentPart()
						.addObject(CreateHeadingsWithStyle.get_title_object_with_style("Heading3", title, 24));
				wordPackage.getMainDocumentPart().addObject(paragraph);

			}
		}

		MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
		sdp = mainDocumentPart.getStyleDefinitionsPart();
		wordPackage.getMainDocumentPart().getStyleDefinitionsPart().setJaxbElement(sdp.getJaxbElement());
		ObjectFactory factory = Context.getWmlObjectFactory();
		RPr makebold = new RPr();
		RFonts font = factory.createRFonts();
		font.setAscii("Arial");
		font.setHAnsi("Arial");
		font.setCs("Arial");
		makebold.setRFonts(font);
		sdp.getDefaultTableStyle().setRPr(makebold);
		sdp.getJaxbElement().getDocDefaults().getRPrDefault().setRPr(makebold);
		Color color = new Color();
		color.setVal("2E74B5");
		RPr colorH1 = new RPr();
		colorH1.setColor(color);
		colorH1.setRFonts(font);
		HpsMeasure hps = new HpsMeasure();
		hps.setVal(BigInteger.valueOf(32));
		colorH1.setSz(hps);
		sdp.getStyleById("Heading1").setRPr(colorH1);
		RPr colorH2 = new RPr();
		colorH2.setColor(color);
		colorH2.setRFonts(font);
		HpsMeasure hps2 = new HpsMeasure();
		hps2.setVal(BigInteger.valueOf(26));
		colorH2.setSz(hps2);
		sdp.getStyleById("Heading2").setRPr(colorH2);
		RPr colorH3 = new RPr();
		colorH3.setColor(color);
		colorH3.setRFonts(font);
		HpsMeasure hps3 = new HpsMeasure();
		hps3.setVal(BigInteger.valueOf(24));
		colorH3.setSz(hps3);
		sdp.getStyleById("Heading3").setRPr(colorH3);

		wordPackage.save(new File(JiraStaticFileURLAddressesDTO.DOCX_FILE_NAME + ".docx"));
	}

}