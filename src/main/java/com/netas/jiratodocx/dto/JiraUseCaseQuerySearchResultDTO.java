package com.netas.jiratodocx.dto;

import java.util.ArrayList;

public class JiraUseCaseQuerySearchResultDTO {
	private String expand;
	private float startAt;
	private float maxResults;
	private float total;
	private ArrayList<Object> issues = new ArrayList<Object>();

	// Getters and Setters
	public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
	}

	public float getStartAt() {
		return startAt;
	}

	public void setStartAt(float startAt) {
		this.startAt = startAt;
	}

	public float getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(float maxResults) {
		this.maxResults = maxResults;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public ArrayList<Object> getIssues() {
		return issues;
	}

	public void setIssues(ArrayList<Object> issues) {
		this.issues = issues;
	}

}