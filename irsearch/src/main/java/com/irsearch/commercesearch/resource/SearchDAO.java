package com.irsearch.commercesearch.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.json.JSONException;

import com.irsearch.commercesearch.model.SearchClusterResults;
import com.irsearch.commercesearch.model.SearchEntity;
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
		List<SearchEntity> entity = new ArrayList<SearchEntity>();
		for(int i=0;i<50;i++){
			SearchEntity e = new SearchEntity();
			try {
				e = sc.searchFiles(query).get(i);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			e.setTitle("Search Result with Rank "+(i+1));
//			e.setDescription("Version 4.4.4 KitKat appeared as a security-only update; it was released on June 19, "
//					+ "2014, shortly after 4.4.3 was released. As of November 2014 [update],"
//					+ " the newest version of the Android operating system, ."
//					+ "Android 5.0 Lollipop, is available for selected devices.");
//			e.setImageUrl("");
//			e.setUrl("https://en.wikipedia.org/wiki/Android_(operating_system)");
			entity.add(e);
		}
		results.setResults(entity);
		results.setInitialQuery(query);
		//TO-DO
		results.setResultCount(1234567);
		long endTime = Calendar.getInstance().getTimeInMillis();
		results.setExecutionTime(endTime-startTime);
		return results;
	}
	public SearchExpansionResults getQueryExpansionSearch(String query){
		//TODO: Ram and Mathew pls fill this up
		long startTime = Calendar.getInstance().getTimeInMillis();
		SearchExpansionResults results = new SearchExpansionResults();
		Searcher sc = new Searcher();
		results.setInitialQuery(query);
		List<SearchEntity> entity = new ArrayList<SearchEntity>();
		for(int i=0;i<50;i++){
			SearchEntity e= new SearchEntity();
			try {
				e = sc.searchExpandedQuery(query).get(i);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			entity.add(e);
		}
		results.setResults(entity);
		//TO-DO
		results.setResultCount(1234567);		
		long endTime = Calendar.getInstance().getTimeInMillis();
		results.setExecutionTime(endTime-startTime);
		return results;
	}
	public SearchClusterResults getClusterSearch(String query){
		//TODO: Wyatt pls fill this up
		return null;
	}
}
