package com.irsearch.commercesearch.service.queryretrival;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONException;
import com.irsearch.commercesearch.init.JSONParser;
import com.irsearch.commercesearch.model.SearchEntity;

public class Searcher {

	public final static String indexDirectoryPath = "IndexData";
	public static List<SearchEntity> finalList = new ArrayList<SearchEntity>();
	public static List<SearchEntity> finalExpList = new ArrayList<SearchEntity>();
	
		
	public static void searchFiles(String srchQuery) throws IOException, ParseException, JSONException{
		finalList = searchIndex(srchQuery);
	    System.out.println(srchQuery);
	    System.out.println(finalList.toString());
	}

	public void searchExpandedQuery(String srchQuery) throws ParseException, IOException, JSONException{
		QueryExpansion qe = new QueryExpansion();
		String expandedQuery = qe.expandQuery(srchQuery, docContent(finalList));
		finalExpList = searchIndex(expandedQuery);
	    System.out.println(expandedQuery);
	    System.out.println(finalExpList.toString());
	}
	
	public static List<SearchEntity> searchIndex(String srchQuery) throws ParseException, IOException, JSONException{
		List<SearchEntity> tempList = new ArrayList<SearchEntity>();
		//JSONParser json = new JSONParser();
		String field = "contents";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectoryPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new StandardAnalyzer();
	    QueryParser parser = new QueryParser(field, analyzer);
	    Query query = parser.parse(srchQuery);
	    System.out.println("Searching for: " + query.toString(field));
	    TopDocs td = searcher.search(query, 50);
	    ScoreDoc[] sd = td.scoreDocs;
	    if(sd.length <= 0){
	    	System.out.println("No matching documents");
	    }
	    for(int i = 0; i < sd.length; i++){
	    	Document doc = searcher.doc(sd[i].doc);
	    	SearchEntity data = JSONParser.docMap.get(doc.get("path"));
	    	System.out.println(data.getUrl());
	    	tempList.add(data);
	    }
	    return tempList;
	}
	
	private ArrayList<String> docContent(List<SearchEntity> rList) {
		ArrayList<String> tempList = new ArrayList<String>();
		int i = 0;
		for(SearchEntity r : rList){
			if(i < 10){
				tempList.add(r.getDescription().substring(0, 200));
			}
			else{
				break;
			}
		}
		return tempList;
	}

	private String optimiseQuery(String searchQuery) {		
		List<String> q = new ArrayList<String>();
		List<String> finalList = new ArrayList<String>();
		q = Arrays.asList(searchQuery.split(" "));
		//If first word is AND, OR or NOT, change it to lower case
		if(q.size() != 0){
			if(q.get(0).equals("AND") || q.get(0).equals("OR") || q.get(0).equals("NOT")){
				finalList.add(q.get(0).toLowerCase());
			}
			else{
				finalList.add(q.get(0));
			}
		}
		if(q.size() > 1){
			for(int i = 1; i < q.size(); i++){
				if(q.get(i).equals("AND") || q.get(i).equals("OR") || q.get(i).equals("NOT")){
					if(!q.get(i+1).equals("AND") && !q.get(i+1).equals("OR") && !q.get(i+1).equals("NOT")){
						finalList.add(q.get(i));
					}
				}
				else{
					finalList.add(q.get(i));
				}
			}
			StringBuilder listString = new StringBuilder();
			for(String s : finalList){
				listString.append(s+" ");
			}
			return listString.toString();
		}
		else{
			return searchQuery;
		}
	}
	
}
