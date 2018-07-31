package com.netas.jiratodocx.main;

import org.junit.runner.JUnitCore;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;

// Arg4j implemented class

public class TestCommandLineParsing {

	// File Name Selection
	@Option(name = "-f", required = true, usage = "Sets file name")
	public static String DOCX_FILE_NAME = "JIRA_DOCX_FILE";

	// Choosing Max Results
	@Option(name = "-m", required = true, usage = "Maximum Results")
	public static String maxResults = "10";

	// Username of JIRA
	@Option(name = "-u", required = true, usage = "Jira Username")
	public static String username;

	// Password of JIRA
	@Option(name = "-p", required = true, usage = "Jira Password")
	public static String password;

	// Query of JIRA
	@Option(name = "-q", required = true, usage = "Jira Query")
	public static String query;

	// Query of JIRA
	@Option(name = "-usecasediagram", required = true, usage = "Use Case Diagram Key(If not exist write 'yok'")
	public static String useCaseDiagramKey = "yok";

	// Query of JIRA
	@Option(name = "-otherdiagram", required = false, usage = "Other Diagram Key(If not exist write 'yok'\")")
	public static String otherDiagramKey = "yok";

	// Query of JIRA
	@Option(name = "-project", required = false, usage = "Traceability(gbs-tuzel)")
	public static String traceability = "gbs";

	public static void main(String[] args) {
		System.exit(new TestCommandLineParsing().run(args));

	}

	private int run(String[] args) {

		CmdLineParser p = new CmdLineParser(this);
		try {
			p.parseArgument(args);
			run();
			return 0;
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			p.printUsage(System.err);
			return 1;
		}
	}

	private void run() {
		// Declare Static Objects
		JiraStaticFileURLAddressesDTO.DOCX_FILE_NAME = DOCX_FILE_NAME;
		JiraStaticFileURLAddressesDTO.maxResult = maxResults;
		JiraStaticFileURLAddressesDTO.username = username;
		JiraStaticFileURLAddressesDTO.password = password;
		JiraStaticFileURLAddressesDTO.diagramCode = useCaseDiagramKey;
		JiraStaticFileURLAddressesDTO.otherDiagramCode = otherDiagramKey;

		query = query.replaceAll(" ", "%20");
		query = query.replaceAll("=", "%3D");
		query = query.replaceAll("\"", "'");

		if (traceability.equalsIgnoreCase("gbs")) {
			JiraStaticFileURLAddressesDTO.numOfReq = 26;
			JiraStaticFileURLAddressesDTO.numOfTSM = 28;
			JiraStaticFileURLAddressesDTO.numOfUseCase = 46;
		} else if (traceability.equalsIgnoreCase("tuzel")) {
			JiraStaticFileURLAddressesDTO.numOfReq = 29;
			JiraStaticFileURLAddressesDTO.numOfTSM = 26;
			JiraStaticFileURLAddressesDTO.numOfUseCase = 45;
		}

		JiraStaticFileURLAddressesDTO.queryCode = query;

		JUnitCore.main("com.netas.jiratodocx.main.JiraToDocxTest");
	}

}
