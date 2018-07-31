package com.netas.jiratodocx.main;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.netas.jiratodocx.dto.JiraStaticFileURLAddressesDTO;
import com.netas.jiratodocx.dto.JiraUseCaseQuerySearchResultDTO;
import com.netas.jiratodocx.service.DocxConverterService;
import com.netas.jiratodocx.service.LogbackToConsole;
import com.netas.jiratodocx.service.ParserService;

public class JiraToDocxTest {

	public static String DOCX_FILE_NAME = JiraStaticFileURLAddressesDTO.DOCX_FILE_NAME;

	public static String JIRA_QUERY_URL = "http://link/rest/api/2/search?jql="
			+ JiraStaticFileURLAddressesDTO.queryCode + "&maxResults=" + JiraStaticFileURLAddressesDTO.maxResult;

	public static String DIAGRAM_QUERY_URL = "http://link/rest/api/2/issue/"
			+ JiraStaticFileURLAddressesDTO.diagramCode;

	public static String OTHER_DIAGRAM_QUERY_URL = "http://link/rest/api/2/issue/"
			+ JiraStaticFileURLAddressesDTO.otherDiagramCode;

	// Test the Project
	@Test
	public void start_parsing_from_jira_to_docx() throws Exception {
		JiraStaticFileURLAddressesDTO.diagramURL = DIAGRAM_QUERY_URL;
		JiraStaticFileURLAddressesDTO.otherDiagramURL = OTHER_DIAGRAM_QUERY_URL;

		ParserService jsonParser = new ParserService();
		DocxConverterService docxConverterService = new DocxConverterService();
		// Hide unnecessary warnings
		LogbackToConsole.disableUnnecessaryMessage();
		// convert result json to java pojo
		JiraUseCaseQuerySearchResultDTO jiraUseCaseQuerySearchResult = jsonParser.readJSONforJira(JIRA_QUERY_URL);
		docxConverterService.writeToDocx(jiraUseCaseQuerySearchResult);
		File file = new File(DOCX_FILE_NAME + ".docx");
		assertTrue(file.exists());
	}
}
