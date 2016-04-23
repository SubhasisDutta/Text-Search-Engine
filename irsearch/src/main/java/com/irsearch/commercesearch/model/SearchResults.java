package com.irsearch.commercesearch.model;

import java.util.List;

public class SearchResults {
	private int resultCount;
	private List<SearchEntity> results;
	private int executionTime;
	
	public int getResultCount() {
		return resultCount;
	}
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	public List<SearchEntity> getResults() {
		return results;
	}
	public void setResults(List<SearchEntity> results) {
		this.results = results;
	}
	public int getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}
}
