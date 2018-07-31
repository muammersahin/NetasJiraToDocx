package com.netas.jiratodocx.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;

public class CreateTSMDescriptions {

	public static WordprocessingMLPackage wordMLPackage;
	public static ArrayList<String> descriptions = new ArrayList<>();
	public static String requirementNumber = "3deQ547TzXLdR50";
	public static final String templatePath = "template.docx";
	public static StyleDefinitionsPart sdp;

	public static String getRequirementNumber() {

		String csvFile = "requirements.csv";
		String line = "";

		boolean requirementNumberFlag = false;
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {

				String[] splitCsv = line.split(";");
				// use comma as separator

				if (splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].contains(JiraStaticFileURLAddressesDTO.KScode)
						&& requirementNumberFlag == false) {

					String number = splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].substring(
							splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].indexOf("_"),
							splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].length());
					number = number.substring(number.indexOf("_"), number.lastIndexOf("_"));
					number = number.substring(number.lastIndexOf("_") + 1, number.lastIndexOf("."));
					requirementNumber = number;
					break;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return requirementNumber;

	}

	public static String initilizeTSM() {

		String csvFile = "requirements.csv";
		String line = "";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {

				String[] splitCsv = line.split(";");
				// use comma as separator

				if (splitCsv[JiraStaticFileURLAddressesDTO.numOfTSM].contains("E")
						&& (splitCsv[1].contains(getRequirementNumber() + "."))) {
					String desc = splitCsv[1].replaceAll("\"", "");
					desc = TurkishCharacterFixService.getFixedRequirements(desc);
					descriptions.add(desc);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return requirementNumber;

	}

	@SuppressWarnings("deprecation")
	public static MainDocumentPart getTSMDescriptions() throws Docx4JException {

		initilizeTSM();
		wordMLPackage = WordprocessingMLPackage.createPackage();
		MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

		WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new File(templatePath));
		MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
		sdp = documentPart.getStyleDefinitionsPart();

		for (int i = 0; i < descriptions.size(); i++) {
			ObjectFactory factory = Context.getWmlObjectFactory();
			P paragraph = factory.createP();
			R rowObject = factory.createR();
			Text t = factory.createText();
			org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
			b.setVal(false);
			RPr makebold = new RPr();
			RFonts font = factory.createRFonts();
			font.setAscii("Arial");
			font.setHAnsi("Arial");
			font.setCs("Arial");
			HpsMeasure hps = new HpsMeasure();
			hps.setVal(BigInteger.valueOf(22));
			makebold.setSz(hps);
			makebold.setRFonts(font);
			makebold.setB(b);
			rowObject.setRPr(makebold);
			rowObject.getContent().clear();
			paragraph.getContent().clear();
			t.setValue(descriptions.get(i) + "\n");
			PPr setLineHeight = new PPr();
			Spacing spacing = new Spacing();
			spacing.setLine(BigInteger.valueOf(360));
			setLineHeight.setSpacing(spacing);
			paragraph.setPPr(setLineHeight);
			rowObject.getContent().add(t);
			paragraph.getContent().add(rowObject);
			mainDocumentPart.addObject(paragraph);
		}

		mainDocumentPart.getStyleDefinitionsPart().setJaxbElement(sdp.getJaxbElement());
		return mainDocumentPart;
	}

}
