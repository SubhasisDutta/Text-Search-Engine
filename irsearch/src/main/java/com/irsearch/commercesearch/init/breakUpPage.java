package com.irsearch.commercesearch.init;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.grizzly.nio.tmpselectors.TemporarySelectorIO;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class breakUpPage {
	
	//TODO: Change this location to the folder to the one that contains all .dat files provided by Crawling
	public static final String INPUT_DAT_FOLDER = "/Users/Matthew_Bachelder/Documents/Development/QE/crawledpages"; 
	// TODO:provide the absolute path to where to store the data files
	public static final String OUTPUT_SEPERATE_DATA_FILES = "/Users/Matthew_Bachelder/Documents/Development/QE/breakup_pages/";
	
	public static Set<String> tempSet = new HashSet<String>();
	
	public static long counter =0;
	
	public static void breakOutFiles(String crawlerFile) {
		   try {
		      BufferedReader br = new BufferedReader(new FileReader(crawlerFile));
		      String line;
		      //int i = 0;

		      while ((line = br.readLine()) != null) {
		         JSONObject obj = new JSONObject(line).getJSONObject("Data");
		         String title = Jsoup.parse(obj.getString("Body")).title();
		         //JSONObject obj2 = new JSONObject(line);
		         if(tempSet.add(title)){
			         obj.put("TITLE", title);

			         PrintWriter out = new PrintWriter(OUTPUT_SEPERATE_DATA_FILES+String.format("%06d.data", counter));
			         out.write(obj.toString());
			         out.close();
			         counter++;
		         }
		      }
		      br.close();
		      
		   } catch (Exception e) {
		      e.printStackTrace();
		   }
		}
	
	public static void main(String[] args){		 
		File files = new File(INPUT_DAT_FOLDER);
		for(File f : files.listFiles()){
			breakOutFiles(f.getAbsolutePath());
		}
	}
}
