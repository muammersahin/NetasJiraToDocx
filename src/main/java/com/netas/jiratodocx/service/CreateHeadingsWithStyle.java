package com.netas.jiratodocx.service;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;

public class CreateHeadingsWithStyle {

	public static P get_title_object_with_style(String val, String title, int size) {

		ObjectFactory factory = Context.getWmlObjectFactory();
		P paragraph = factory.createP();
		R rowObject = factory.createR();
		Text t = factory.createText();

		RPr makebold = new RPr();
		RFonts font = factory.createRFonts();
		font.setAscii("Arial");
		font.setHAnsi("Arial");
		font.setCs("Arial");
		HpsMeasure hps = new HpsMeasure();
		hps.setVal(BigInteger.valueOf(size));
		makebold.setSz(hps);
		makebold.setRFonts(font);
		rowObject.setRPr(makebold);
		org.docx4j.wml.PPrBase.PStyle pstyle = new org.docx4j.wml.PPrBase.PStyle();
		pstyle.setVal(val);
		PPr paragraphStyle = new PPr();
		paragraphStyle.setPStyle(pstyle);
		paragraph.setPPr(paragraphStyle);

		t.setValue(title);

		rowObject.getContent().add(t);
		paragraph.getContent().add(rowObject);

		return paragraph;

	}

}
