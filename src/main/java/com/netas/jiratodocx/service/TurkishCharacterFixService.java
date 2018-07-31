package com.netas.jiratodocx.service;

public class TurkishCharacterFixService {

	public static String getFixedHeadings(String title) {

		title = title.replaceAll(".png", "");
		title = title.replaceAll(".jpg", "");
		title = title.replaceAll(".PNG", "");
		title = title.replaceAll(".JPG", "");

		title = title.replaceAll("\\+", " ");
		// LowerCase characters
		title = title.replaceAll("%C4%B1", "ı");
		title = title.replaceAll("%C5%9F", "ş");
		title = title.replaceAll("%C3%B6", "ö");
		title = title.replaceAll("%C3%BC", "ü");
		title = title.replaceAll("%C4%9F", "ğ");
		title = title.replaceAll("%C3%A7", "ç");
		// UpperCase characters
		title = title.replaceAll("%C4%B0", "İ");
		title = title.replaceAll("%C3%96", "Ö");
		title = title.replaceAll("%C3%9C", "Ü");
		title = title.replaceAll("%C4%9E", "Ğ");
		title = title.replaceAll("%C3%87", "Ç");
		title = title.replaceAll("%C5%9E", "Ş");
		// Symbols
		title = title.replaceAll("%28", "(");
		title = title.replaceAll("%29", ")");

		return title;

	}

	public static String getFixedRequirements(String content) {
		content = content.replaceAll("<span style=\"\"font-weight:bold\"\">", "");
		content = content.replaceAll("</span>", "");
		content = content.replaceAll("<ul>", "");
		content = content.replaceAll("</ul>", "");
		content = content.replaceAll("<li>", "");
		content = content.replaceAll("</li>", "");
		content = content.replaceAll("<p>", "");
		content = content.replaceAll("</p>", "");
		content = content.replaceAll("ÅŸ", "ş");
		content = content.replaceAll("Ä±", "ı");
		content = content.replaceAll("Ã¶", "ö");
		content = content.replaceAll("Ã§", "ç");
		content = content.replaceAll("Ã¼", "ü");
		content = content.replaceAll("ÄŸ", "ğ");
		content = content.replaceAll("Ã–", "ö");
		content = content.replaceAll("â€¢", "•");
		content = content.replaceAll("Ã‡", "Ç");
		content = content.replaceAll("â€™", "’");
		content = content.replaceAll("Ä°", "İ");
		content = content.replaceAll("Ãœ", "Ü");
		content = content.replaceAll("Â", "");
		content = content.replaceAll("Ã¢", "â");

		return content;

	}

}
