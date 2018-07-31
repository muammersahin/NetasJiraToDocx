package com.netas.jiratodocx.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;

public class CreateFunctionalRequirements {

	public static WordprocessingMLPackage wordMLPackage;
	public static ArrayList<String> requirementList = new ArrayList<>();
	public static ArrayList<String> requirementMatris = new ArrayList<>();
	public static TreeMap<String, String> sorted = null;
	public static ParserService parser = new ParserService();

	public static int getRowCount() {

		String csvFile = "requirements.csv";
		String line = "";
		int rowCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {

				String[] splitCsv = line.split(";");
				// use comma as separator
				String requirementCode = "KSCODE";
				if (splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].contains("YIM")) {
					requirementCode = splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", "");
					requirementCode = requirementCode.replaceAll("YIM", "YİM");
					requirementCode.replaceAll("<I>", "İ");

				}

				if (splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].contains(JiraStaticFileURLAddressesDTO.KScode)) {
					rowCount++;
					requirementList.add(splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", ""));
					requirementList.add(splitCsv[1].replaceAll("\"", ""));
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return rowCount;

	}

	public static int getRmsisMatris() throws Exception {

		String csvFile = "requirements.csv";
		String line = "";
		int rowCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {

				String[] splitCsv = line.split(";");
				// use comma as separator
				String requirementCode = "KSCODE";
				if (splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].contains("YIM")) {
					requirementCode = splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", "");
					requirementCode = requirementCode.replaceAll("YIM", "YİM");
					requirementCode.replaceAll("<I>", "İ");

				}

				if (splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].contains(JiraStaticFileURLAddressesDTO.KScode)
						&& splitCsv.length >= JiraStaticFileURLAddressesDTO.numOfUseCase) {
					splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase] = splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase]
							.replaceAll(" ", "");
					splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase] = splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase]
							.replaceAll("\"", "");
					if (splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase] != null
							&& splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase] != ""
							&& splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase] != " ") {
						String codes = "";
						String[] splitCodes = splitCsv[JiraStaticFileURLAddressesDTO.numOfUseCase].split(",");
						codes = codes.replaceAll(parser.parseRequirementInfo(splitCodes[0].replaceAll("\"", "")), "")
								+ parser.parseRequirementInfo(splitCodes[0].replaceAll("\"", ""));
						for (int i = 1; i < splitCodes.length; i++) {
							codes = codes.replaceAll(parser.parseRequirementInfo(splitCodes[i].replaceAll("\"", "")),
									"") + ", " + parser.parseRequirementInfo(splitCodes[i].replaceAll("\"", ""));
						}

						if (!JiraStaticFileURLAddressesDTO.rmsisMatris
								.containsKey(splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", ""))) {
							JiraStaticFileURLAddressesDTO.rmsisMatris.put(
									splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", ""),
									codes.replaceAll("\"", ""));
						} else {
							String temp = JiraStaticFileURLAddressesDTO.rmsisMatris
									.get(splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", ""));
							temp = codes;
							JiraStaticFileURLAddressesDTO.rmsisMatris
									.put(splitCsv[JiraStaticFileURLAddressesDTO.numOfReq].replaceAll("\"", ""), temp);
						}
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		sorted = new TreeMap<>(JiraStaticFileURLAddressesDTO.rmsisMatris);

		for (String key : sorted.keySet()) {

			if (sorted.get(key) != null && sorted.get(key).length() > 1)
				requirementMatris.add(key);

		}

		return rowCount;

	}

	public static WordprocessingMLPackage createTable() throws Docx4JException {
		wordMLPackage = WordprocessingMLPackage.createPackage();
		MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

		int title = 0;
		ObjectFactory factory = Context.getWmlObjectFactory();

		int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0).getPageDimensions()
				.getWritableWidthTwips();
		int columnNumber = 3;
		int index = 0;
		Tbl tbl = TblFactory.createTable(getRowCount() + 1, 2, writableWidthTwips / columnNumber);
		TblPr tableProperties = tbl.getTblPr();
		TblWidth tableWith = new TblWidth();
		tableWith.setW(BigInteger.valueOf(10000));
		tableProperties.setTblW(tableWith);
		tbl.setTblPr(tableProperties);
		List<Object> rows = tbl.getContent();
		for (Object row : rows) {
			Tr tr = (Tr) row;
			List<Object> cells = tr.getContent();
			for (Object cell : cells) {
				P paragraph = factory.createP();
				R rowObject = factory.createR();
				Text t = factory.createText();
				Tc td = (Tc) cell;
				TcPr cellProperties = new TcPr();

				cellProperties.setTcW(tableWith);
				td.setTcPr(cellProperties);
				if (title == 0) {

					t.setValue("Gereksinim Kodu");

					rowObject.getContent().clear();
					rowObject.getContent().add(t);
					paragraph.getContent().clear();
					RPr makebold = new RPr();
					RFonts font = factory.createRFonts();
					font.setAscii("Arial");
					font.setHAnsi("Arial");
					font.setCs("Arial");
					HpsMeasure hps = new HpsMeasure();
					hps.setVal(BigInteger.valueOf(22));
					makebold.setSz(hps);
					makebold.setRFonts(font);
					org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
					makebold.setB(b);
					rowObject.setRPr(makebold);
					PPr setLineHeight = new PPr();
					Spacing spacing = new Spacing();
					spacing.setLine(BigInteger.valueOf(360));
					setLineHeight.setSpacing(spacing);
					paragraph.setPPr(setLineHeight);
					paragraph.getContent().add(rowObject);
					title++;

				} else if (title == 1) {
					t.setValue("Gereksinim");
					rowObject.getContent().clear();
					rowObject.getContent().add(t);
					RPr makebold = new RPr();
					RFonts font = factory.createRFonts();
					font.setAscii("Arial");
					font.setHAnsi("Arial");
					font.setCs("Arial");
					HpsMeasure hps = new HpsMeasure();
					hps.setVal(BigInteger.valueOf(22));
					makebold.setSz(hps);
					makebold.setRFonts(font);
					org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
					makebold.setB(b);
					rowObject.setRPr(makebold);
					PPr setLineHeight = new PPr();
					Spacing spacing = new Spacing();
					spacing.setLine(BigInteger.valueOf(360));
					setLineHeight.setSpacing(spacing);
					paragraph.setPPr(setLineHeight);
					paragraph.getContent().clear();
					paragraph.getContent().add(rowObject);
					title++;
				} else {
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
					if (index < requirementList.size()) {
						String content = requirementList.get(index);
						content = TurkishCharacterFixService.getFixedRequirements(content);

						t.setValue(content);
					}
					PPr setLineHeight = new PPr();
					Spacing spacing = new Spacing();
					spacing.setLine(BigInteger.valueOf(360));
					setLineHeight.setSpacing(spacing);
					paragraph.setPPr(setLineHeight);
					rowObject.getContent().clear();
					rowObject.getContent().add(t);
					paragraph.getContent().clear();
					paragraph.getContent().add(rowObject);
					index++;
				}
				td.getContent().add(paragraph);
			}
		}

		mainDocumentPart.getContent().add(tbl);

		return wordMLPackage;

	}

	public static WordprocessingMLPackage getRequirementMatrix() throws Exception {
		getRmsisMatris();
		wordMLPackage = WordprocessingMLPackage.createPackage();
		MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

		boolean worked = false;
		int title = 0;
		ObjectFactory factory = Context.getWmlObjectFactory();

		int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0).getPageDimensions()
				.getWritableWidthTwips();
		int columnNumber = 3;
		int index = 0;
		Tbl tbl = TblFactory.createTable(sorted.size(), 2, writableWidthTwips / columnNumber);
		TblPr tableProperties = tbl.getTblPr();
		TblWidth tableWith = new TblWidth();
		tableWith.setW(BigInteger.valueOf(10000));
		tableProperties.setTblW(tableWith);
		tbl.setTblPr(tableProperties);
		List<Object> rows = tbl.getContent();
		for (Object row : rows) {
			Tr tr = (Tr) row;
			List<Object> cells = tr.getContent();
			for (Object cell : cells) {
				P paragraph = factory.createP();
				R rowObject = factory.createR();
				Text t = factory.createText();
				Tc td = (Tc) cell;
				TcPr cellProperties = new TcPr();

				cellProperties.setTcW(tableWith);
				td.setTcPr(cellProperties);
				if (title == 0) {

					t.setValue("Gereksinim Kodu");

					rowObject.getContent().clear();
					rowObject.getContent().add(t);
					PPr setLineHeight = new PPr();
					Spacing spacing = new Spacing();
					spacing.setLine(BigInteger.valueOf(360));
					setLineHeight.setSpacing(spacing);
					paragraph.setPPr(setLineHeight);
					paragraph.getContent().clear();
					RPr makebold = new RPr();
					RFonts font = factory.createRFonts();
					font.setAscii("Arial");
					font.setHAnsi("Arial");
					font.setCs("Arial");
					HpsMeasure hps = new HpsMeasure();
					hps.setVal(BigInteger.valueOf(22));
					makebold.setSz(hps);
					makebold.setRFonts(font);
					org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();

					makebold.setB(b);
					rowObject.setRPr(makebold);
					paragraph.getContent().add(rowObject);
					title++;

				} else if (title == 1) {
					t.setValue("Kullanım Senaryosu");
					rowObject.getContent().clear();
					rowObject.getContent().add(t);
					RPr makebold = new RPr();
					RFonts font = factory.createRFonts();
					font.setAscii("Arial");
					font.setHAnsi("Arial");
					font.setCs("Arial");
					HpsMeasure hps = new HpsMeasure();
					hps.setVal(BigInteger.valueOf(22));
					makebold.setSz(hps);
					makebold.setRFonts(font);
					org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
					makebold.setB(b);
					rowObject.setRPr(makebold);
					PPr setLineHeight = new PPr();
					Spacing spacing = new Spacing();
					spacing.setLine(BigInteger.valueOf(360));
					setLineHeight.setSpacing(spacing);
					paragraph.setPPr(setLineHeight);
					paragraph.getContent().clear();
					paragraph.getContent().add(rowObject);
					title++;
				} else {
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
					String content = "Yok";
					if (index < sorted.keySet().size() && index < requirementMatris.size()) {
						if (worked == false) {
							content = requirementMatris.get(index);
							worked = true;
						} else {

							content = sorted.get(requirementMatris.get(index));
							worked = false;
							index++;
						}

						t.setValue(content);
					}
					rowObject.getContent().clear();
					rowObject.getContent().add(t);
					PPr setLineHeight = new PPr();
					Spacing spacing = new Spacing();
					spacing.setLine(BigInteger.valueOf(360));
					setLineHeight.setSpacing(spacing);
					paragraph.setPPr(setLineHeight);
					paragraph.getContent().clear();
					paragraph.getContent().add(rowObject);
				}
				td.getContent().add(paragraph);
			}
		}

		mainDocumentPart.getContent().add(tbl);

		return wordMLPackage;
	}

}
