package com.irsearch.commercesearch.resource;

import com.irsearch.commercesearch.model.SearchClusterResults;
import com.irsearch.commercesearch.model.SearchExpansionResults;
import com.irsearch.commercesearch.model.SearchResults;

public class SearchDAO implements iSearchDAO {
	public SearchResults getQuerySearch(String query){
		//TODO: This method will do what ever you want to do to get the result 
		//... for reference pls chcl SearchStubDAO 
		//... for checking if results are comming please use the SearchDAOTest and SearchStubDAOTest 
		//... u can run them as jUnit test files 
		return null;
	}
	public SearchExpansionResults getQueryExpansionSearch(String query){
		//TODO: Ram and Mathew pls fill this up
		return null;
	}
	public SearchClusterResults getClusterSearch(String query){
		//TODO: Wyatt pls fill this up
		return null;
	}
}
