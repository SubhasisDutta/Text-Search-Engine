package com.irsearch.commercesearch.resource;

import java.io.IOException;
import java.util.Calendar;

import org.apache.lucene.queryparser.classic.ParseException;
import org.json.JSONException;

import com.irsearch.commercesearch.model.SearchClusterResults;
import com.irsearch.commercesearch.model.SearchExpansionResults;
import com.irsearch.commercesearch.model.SearchResults;
import com.irsearch.commercesearch.service.queryretrival.Searcher;

public class SearchDAO implements iSearchDAO {
	public SearchResults getQuerySearch(String query){
		//TODO: This method will do what ever you want to do to get the result 
		//... for reference pls chcl SearchStubDAO 
		//... for checking if results are comming please use the SearchDAOTest and SearchStubDAOTest 
		//... u can run them as jUnit test files 
		long startTime = Calendar.getInstance().getTimeInMillis();
		SearchResults results = new SearchResults();
		Searcher sc = new Searcher();
		try {
			results = sc.searchFiles(query);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = Calendar.getInstance().getTimeInMillis();
		results.setExecutionTime(endTime-startTime);
		return results;
	}
	public SearchExpansionResults getQueryExpansionSearch(String query){
		//TODO: Ram and Mathew pls fill this up
		long startTime = Calendar.getInstance().getTimeInMillis();
		SearchExpansionResults results = new SearchExpansionResults();
		Searcher sc = new Searcher();
		try {
			results = sc.searchExpandedQuery(query);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results.setInitialQuery(query);
		long endTime = Calendar.getInstance().getTimeInMillis();
		results.setExecutionTime(endTime-startTime);
		return results;
	}
	public SearchClusterResults getClusterSearch(String query){
		//TODO: Wyatt pls fill this up
		return null;
	}
}
