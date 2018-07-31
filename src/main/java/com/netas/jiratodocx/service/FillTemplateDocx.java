package com.netas.jiratodocx.service;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;

public class FillTemplateDocx {

	public static final String templatePath = "template.docx";
	public static WordprocessingMLPackage wordMLPackage;
	public static StyleDefinitionsPart sdp;

	// inilialize the main Docx file
	public static void initialize() throws Docx4JException {

		wordMLPackage = WordprocessingMLPackage.createPackage();

	}

	// add Replaced variables to docx
	public static void replaceVariablesInTemplate(Map<String, String> mapping, String docxName,
			ArrayList<String> imageUrls) throws Exception {
		WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new File(templatePath));
		MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
		sdp = documentPart.getStyleDefinitionsPart();
		documentPart.variableReplace(mapping);
		// Adding documents to a single file
		ObjectFactory factory = Context.getWmlObjectFactory();
		P title = (P) documentPart.getContent().get(0);
		title.getContent().get(0);
		R rowObject = factory.createR();
		rowObject = (R) title.getContent().get(0);
		title.getContent().clear();
		RPr makebold = new RPr();
		RFonts font = factory.createRFonts();
		font.setAscii("Arial");
		font.setHAnsi("Arial");
		font.setCs("Arial");
		HpsMeasure hps = new HpsMeasure();
		hps.setVal(BigInteger.valueOf(26));
		makebold.setSz(hps);
		makebold.setRFonts(font);
		rowObject.setRPr(makebold);
		org.docx4j.wml.PPrBase.PStyle pstyle = new org.docx4j.wml.PPrBase.PStyle();
		pstyle.setVal("Heading2");
		PPr paragraphStyle = new PPr();
		paragraphStyle.setPStyle(pstyle);
		title.setPPr(paragraphStyle);

		title.getContent().add(rowObject);

		documentPart.getContent().set(0, title);

		wordMLPackage.getMainDocumentPart().addObject(documentPart.getContent().get(0));
		wordMLPackage.getMainDocumentPart().addObject(documentPart.getContent().get(1));
	}

	@SuppressWarnings("deprecation")
	// Create the file
	public static void createDocxFile() throws Docx4JException {
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
		documentPart.getStyleDefinitionsPart().setJaxbElement(sdp.getJaxbElement());
		wordMLPackage.save(new File("temp.docx"));

	}

}
