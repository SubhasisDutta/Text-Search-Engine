package com.irsearch.commercesearch.init;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.irsearch.commercesearch.model.SearchEntity;


public class JSONParser {

	public static Map<String, SearchEntity> docMap = new HashMap<String, SearchEntity>();
	
	public String readJSON(String filePath, InputStream inputStream) {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		String line;
		SearchEntity res = new SearchEntity();
		try {
			while((line = br.readLine()) != null){
				try {
					JSONObject json = new JSONObject(line);
					String url = json.getJSONObject("Data").getString("Url");
					String body = json.getJSONObject("Data").getString("Body");
					JSONObject obj = new JSONObject(line).getJSONObject("Data");
			        String title = Jsoup.parse(obj.getString("Body")).title();
					res.setUrl(url);
					res.setTitle(title);
					res.setDescription(body);
					docMap.put(filePath, res);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
