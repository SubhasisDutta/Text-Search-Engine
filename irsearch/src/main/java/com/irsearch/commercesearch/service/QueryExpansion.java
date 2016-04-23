package com.irsearch.commercesearch.service;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import opennlp.tools.stemmer.PorterStemmer;


public class QueryExpansion {
		
    private HashMap<String, Integer> stopWords;
    private HashMap<String, Integer> originalQuery;
    private ArrayList<String> [] fileContents;
    QueryExpansion() {
        stopWords = new HashMap<String, Integer>();
         
        loadStopWords();
    }

    private void loadStopWords() {

        try {
            Scanner scanner = new Scanner(new File("stopwords"));

            while (scanner.hasNextLine()) {
                stopWords.put(scanner.nextLine(), Integer.MIN_VALUE);
            }

        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        }

    }
    
    
    public String expandQuery2(String oldQuery, ArrayList<String> files){
        String newQuery ="";
        int releventCollectionSize = files.size(); //|N|
        int localVocabularySize = 0; //|V|
        int localStemSize = 0; //|S|
        fileContents = new ArrayList[releventCollectionSize];
        originalQuery = getQuery(oldQuery); //holds the query stems. will update with the matrix row later
        
        
        //maintain a list of local vocabualary found in relevent documents as well as the ount for each document.
        //will need this for the association matrix
        HashMap<String, HashMap<Integer, Integer>> localVocab = getLocalVocabulary(files);
        localVocabularySize = localVocab.size();
        HashMap<String, String> localStemmedVocab = getLocalStemmedVocabulary(localVocab);
        localStemSize = localStemmedVocab.size();
        
        ArrayList<Integer> newQueryTermRows = new ArrayList<Integer>();
        
        /* matrix with rows of stems and columns of documents. one for each Query term?? I think so...*/
        
        newQuery = oldQuery;
        for(String key: originalQuery.keySet()){
            
            String stem = key;
            
            PorterStemmer stemmer = new PorterStemmer();
            stem = stemmer.stem(stem);//set string you need to stem
            
            
            double[][] correllationMatrix = getMetricCorrellation(releventCollectionSize, 
                    localStemSize, stem, localStemmedVocab, localVocab, files);
            
          double[] topTwoCorrellation = new double[2];
          int[] topTwoTerms = new int[2];
          topTwoCorrellation[0] = 0.0;
          topTwoCorrellation[1] =0.0;
          topTwoTerms[0] = 0;
          topTwoTerms[1] =0;
          
          for(int i=0; i<localStemSize; i++){
              double current = 0.0;
              for(int j=0;j <releventCollectionSize; j++){
                  current+= correllationMatrix[i][j];
                  
              }
              
              if(current > topTwoCorrellation[0]){
                  double temp = topTwoCorrellation[0];
                  int tempRow = topTwoTerms[0];
                  topTwoCorrellation[0] = current;
                  topTwoTerms[0] = i;
                  
                  if(temp > topTwoCorrellation[1] ){
                      topTwoCorrellation[1]  = temp;
                      topTwoTerms[1] = tempRow;
                      
                  }
              }else if(current > topTwoCorrellation[1]){
                  topTwoCorrellation[1] = current;
                  topTwoTerms[1] = i;
              }
              
          }
          
          //add to expadned query
          newQueryTermRows.add(topTwoTerms[1]);
          newQueryTermRows.add(topTwoTerms[0]);
          
        }
        
        newQuery = oldQuery+" "+getNewQueryTerms(newQueryTermRows,localStemmedVocab);
        return newQuery;
    }

    private double [][] getMetricCorrellation(int collectionSize, int localStemSize, 
                                            String stem, HashMap<String, 
                                            String> localStemmedVocab, 
                                            HashMap<String, HashMap<Integer, Integer>> localVocab ,
                                            ArrayList<String> files ){
        
        
        double [][] matrix = new double[localStemSize][collectionSize];
        
        
        Map<String, String> sortedMap = new TreeMap<String, String>(localStemmedVocab);
    
        for(int j=0; j < fileContents.length; j++){
            int row =0;
            String  keywordI = stem;
            for(String key: sortedMap.keySet()){
                
                double correllation = 0.0;
                String keywordJ = key;
                System.out.println(keywordI+","+keywordJ);
                    
                    double currentGap = 0.0;
                    
                        boolean found = false;
                        for(int g=0; g<fileContents[j].size(); g++){
                               currentGap = 0; 
                               
                               if(fileContents[j].get(g).compareTo(keywordI)==0){
                                   //count the words until we reach keyword j
                                   found = true;
                                   currentGap =1;
                                   g++;
                                   while(found && g<fileContents[j].size()){
                                       if(fileContents[j].get(g).compareTo(keywordJ)!=0)
                                           currentGap++;
                                       else{
                                           found = false;
                                           
                                       }
                                       g++;
                                   }
                               }else if(fileContents[j].get(g).compareTo(keywordJ)==0){
                                   //count the words until we reach keyword i
                                   found = true;
                                   currentGap =1;
                                   g++;
                                   while(found && g<fileContents[j].size()){
                                       if(fileContents[j].get(g).compareTo(keywordI)!=0)
                                           currentGap++;
                                       else{
                                           found = false;
                                           
                                       }
                                       g++;
                                   }
                                   
                               }
                               //otherwise do nothing and continue
                               //if currentGap is greate than 0 and found is = false then we have found something in this iteration and we should add to correlation?
                               if(!found && currentGap>0){
                                  correllation+= 1.0/currentGap; 
                                   
                               }
                               
                        }
                        
                    
                    
                
                 
                //j is doument number
                matrix[row][j]= correllation;
                row++;
            }
        }
        
        return matrix;
        
    }
    
    
    
    public String expandQuery(String oldQuery, ArrayList<String> files) {

        String newQuery = "";
        int releventCollectionSize = files.size(); //|N|
        int localVocabularySize = 0; //|V|
        int localStemSize = 0; //|S|
        fileContents = new ArrayList[releventCollectionSize];

        originalQuery = getQuery(oldQuery); //holds the query stems. will update with the matrix row later

        
        //maintain a list of local vocabualary found in relevent documents as well as the ount for each document.
        //will need this for the association matrix
        HashMap<String, HashMap<Integer, Integer>> localVocab = getLocalVocabulary(files);
        localVocabularySize = localVocab.size();
        HashMap<String, String> localStemmedVocab = getLocalStemmedVocabulary(localVocab);
        localStemSize = localStemmedVocab.size();
        
        //check to make sure that the terms from the original query appear at least once in the documents.
        //if not return original query
        
        int count =0;
        for(String key: originalQuery.keySet())
            if(localStemmedVocab.containsKey(key))
                count++;
        
        if(count == 0 )
            return oldQuery;
        
        //make the association matrix

         //fill association matrix
        int[][] associationMatrix = fillAssocMatrix(localStemSize,
                                                    releventCollectionSize,
                                                    localVocab,
                                                    localStemmedVocab);


       ArrayList<Integer> newQueryTermRows = new ArrayList<Integer>();
       
       //TODO add the original query to the new Query term rows, so we do not duplicate.

       //first we need to get the row each query term reside in

       for(String key: originalQuery.keySet()){
           newQueryTermRows.add(originalQuery.get(key));
           double [] associationMatrixCorrellations = new double[localStemSize];

            for(int i =0; i<localStemSize; i++){
                //stores each value from the computed correclation against each stem
                double cuv = 0;
                double cuu =0;
                double cvv = 0;
                if(i!=originalQuery.get(key) && originalQuery.get(key)!=Integer.MIN_VALUE){
                for(int j=0; j<releventCollectionSize; j++){
                    cuv+= associationMatrix[originalQuery.get(key)][j]* associationMatrix[i][j];
                    cuu+= associationMatrix[originalQuery.get(key)][j]*associationMatrix[originalQuery.get(key)][j];
                    cvv += associationMatrix[i][j] * associationMatrix[i][j];
                }
                }
               associationMatrixCorrellations[i] = (cuv/(cuv+cuu+cvv));
            }
            double [] topTwo = new double[2];
            int [] topTwoRows = new int[2];
            
            for(int i=0; i<localStemSize; i++){
                if(i<2){
                    topTwo[i]= associationMatrixCorrellations[i];
                    topTwoRows[i] = i;
                }
                else{
                    if(associationMatrixCorrellations[i]>topTwo[0]){
                        double temp = topTwo[0];
                        int tempRow = topTwoRows[0];
                        topTwo[0] = associationMatrixCorrellations[i];
                        topTwoRows[0]=i;
                        if(temp > topTwo[1]){
                            topTwo[1] = temp;
                            topTwoRows[1] = tempRow;
                        }

                    }else{
                        if(associationMatrixCorrellations[i]>topTwo[1]){
                            topTwo[1] = associationMatrixCorrellations[i];
                            topTwoRows[1] = i;
                        }
                }
            }
            }
            
            boolean[] inserted = new boolean[2];
            inserted[0] = false;
            inserted[1] = false;
            
            for(int i=0; i< newQueryTermRows.size(); i++){
               if(newQueryTermRows.get(i)== topTwoRows[1] && !inserted[1]){
                  inserted[1] = true;
                  if(inserted[0])
                        break;
               }
               if(newQueryTermRows.get(i)== topTwoRows[0] && !inserted[0]){
                   inserted[0] = true;
                  if(inserted[1])
                        break;
                   
               }
               
            }
            
            if(!inserted[0]){
                newQueryTermRows.add(topTwoRows[0]);
                
            }
            if(!inserted[1]){
                newQueryTermRows.add(topTwoRows[1]);
            }    
            
       }
       
       newQuery = /*oldQuery+" "+*/getNewQueryTerms(newQueryTermRows,localStemmedVocab);

        //Map<String, Integer> sortedMap = new TreeMap<String, Integer>(localVocab);
        return newQuery;
    }

    
    private String getNewQueryTerms(ArrayList<Integer> newQueryTermRows , HashMap<String, String> localStemmedVocab){
        String query ="";
        
        Map<String, String> sortedMap = new TreeMap<String, String>(localStemmedVocab);

        for(int i=0; i <newQueryTermRows.size(); i++){
        int row = 0; //increment as we move forward in the list of stems
        int rowNeeded = newQueryTermRows.get(i);
        
        for(String key: sortedMap.keySet()){
            if(rowNeeded == row){
             
             //if(!query.contains(key))
               String [] word = sortedMap.get(key).split(",");
             
                query+= word[0]+" ";
             break;
             
            }
            row++;
        }
        }
        return query;
    }

    private HashMap<String, Integer> getQuery(String line){

        HashMap<String, Integer> query = new HashMap<String, Integer>();
        line = line.replace("\n", " ").replace("\r", " ").toLowerCase().replaceAll("[^a-z]", " ");
        StringTokenizer tokens = new StringTokenizer(line);
        while (tokens.hasMoreTokens()) {
                String nextToken = tokens.nextToken();
                if (!stopWords.containsKey(nextToken) && nextToken.length() > 2) {
                    String stem = nextToken;
                    PorterStemmer stemmer = new PorterStemmer();
                    stem = stemmer.stem(stem);//set string you need to stem

                    if(!query.containsKey(stem)){
                        query.put(stem, Integer.MIN_VALUE);
                    }
                }
        }
        return query;
    }

    private int[][] fillAssocMatrix(int localStemSize, int releventCollectionSize,
                            HashMap<String, HashMap<Integer, Integer>> localVocab,
                            HashMap<String, String> localStemmedVocab){


        int[][] associationMatrix = new int[localStemSize][releventCollectionSize];

        Map<String, String> sortedMap = new TreeMap<String, String>(localStemmedVocab);

        int row = 0; //increment as we move forward in the list of stems
        for(String key: sortedMap.keySet()){

            String [] words = sortedMap.get(key).split(",");

            if(originalQuery.containsKey(key))
                originalQuery.put(key, row);

            for(int j =0; j<words.length; j++){ //stem is key words[j] is assococaited.
                                                //update the column count for each document where the words[j] is found
                HashMap<Integer, Integer> docs = localVocab.get(words[j]);

                for(Integer doc: docs.keySet()){
                    associationMatrix[row][doc]+= docs.get(doc);
                }

            }
            
            row++;
        }


        return  associationMatrix;
    }

    private HashMap<String, String> getLocalStemmedVocabulary(HashMap<String, HashMap<Integer, Integer>> localVocab) {
//TODO: come back and see if we cant make a better stemmer? this is a porter stemmer and she mentioned we cannot have that.
        HashMap<String, String> stemmedVocab = new HashMap<String, String>();
        
        for (String key : localVocab.keySet()) {

            String stem = key;
            PorterStemmer stemmer = new PorterStemmer();
            stem = stemmer.stem(stem);//set string you need to stem

            if(stemmedVocab.containsKey(stem))
                stemmedVocab.put(stem, stemmedVocab.get(stem)+","+key);
            else
                stemmedVocab.put(stem, key);
        }

        return stemmedVocab;
    }

    HashMap<String, HashMap<Integer, Integer>> getLocalVocabulary(ArrayList<String> files) {

        HashMap<String, HashMap<Integer, Integer>>localVocab = new HashMap<String, HashMap<Integer, Integer>>();
              //<term, HashMap<doc#, tf>>
              //fileContents.. go ahead and store the file in file contents. we are goign to need it anyway...
        for (int i = 0; i < files.size(); i++) {
            fileContents[i] =  new ArrayList<String>();
            String line = files.get(i);
            
                
            line = line.replace("\n", " ").replace("\r", " ").toLowerCase().replaceAll("[^a-z]", " ");

            StringTokenizer tokens = new StringTokenizer(line);

            while (tokens.hasMoreTokens()) {
                String nextToken = tokens.nextToken();
                
                String stem = nextToken;
                PorterStemmer stemmer = new PorterStemmer();
                stem = stemmer.stem(stem);//set string you need to stem
                fileContents[i].add(stem);
                if (!stopWords.containsKey(nextToken) && nextToken.length() > 2) {

                    if (localVocab.containsKey(nextToken)) {
                        HashMap<Integer, Integer> t = localVocab.get(nextToken);
                        if(t.containsKey(i))
                            t.put(i, t.get(i)+1);
                        else
                            t.put(i, 1);
                        localVocab.put(nextToken, t);
                    } else {
                        HashMap<Integer, Integer> t = new HashMap<Integer, Integer>();
                        t.put(i, 1);
                        localVocab.put(nextToken, t);
                    }
                }
            }
        }
        return localVocab;
    }

}
