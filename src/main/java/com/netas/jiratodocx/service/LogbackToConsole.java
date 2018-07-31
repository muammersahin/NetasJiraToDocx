package com.netas.jiratodocx.service;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogbackToConsole {

	// Disable Unnecessary Warnings
	public static void disableUnnecessaryMessage() {
		Logger.getRootLogger().setLevel(Level.INFO);
		System.err.close();
		System.setErr(System.out);
	}

}
