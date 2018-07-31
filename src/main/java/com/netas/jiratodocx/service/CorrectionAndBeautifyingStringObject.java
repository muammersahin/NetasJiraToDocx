package com.netas.jiratodocx.service;

import java.util.regex.Pattern;

public class CorrectionAndBeautifyingStringObject {

	public static String string_corrector(String input) throws InterruptedException {

		input = input.replaceAll("\t", " ");
		String[] lines = input.split("\n");
		String output = "";

		for (int i = 0; i < lines.length; i++) {

			// Regular Expressions
			Pattern pattern1 = Pattern.compile("\\d{1,}\\.\\d{1,}[\\.|\\s]");
			Pattern pattern2 = Pattern.compile("\\d{1,}\\.\\d{1,}.\\d{1,}[\\.|\\s]");
			Pattern pattern3 = Pattern.compile("\\d{1,}\\.\\d{1,}.\\d{1,}.\\d{1,}[\\.|\\s]");
			Pattern pattern4 = Pattern.compile("\\d{1,}\\.\\d{1,}.\\d{1,}.\\d{1,}.\\d{1,}[\\.|\\s]");

			// Find the pattern and insert space
			if (pattern4.matcher(lines[i]).find()) {

				output = output + "</w:t><w:t xml:space=\"preserve\">        " + lines[i] + "\n";

			} else if (pattern3.matcher(lines[i]).find()) {

				output = output + "</w:t><w:t xml:space=\"preserve\">      " + lines[i] + "\n";

			} else if (pattern2.matcher(lines[i]).find()) {

				output = output + "</w:t><w:t xml:space=\"preserve\">    " + lines[i] + "\n";

			} else if (pattern1.matcher(lines[i]).find()) {

				output = output + "</w:t><w:t xml:space=\"preserve\">  " + lines[i] + "\n";

			} else {

				output = output + lines[i] + "\n";

			}

		}

		return output;
	}

}
