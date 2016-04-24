package com.irsearch.commercesearch.service.cluster;

import com.irsearch.commercesearch.model.SearchClusterResults;

import com.irsearch.commercesearch.model.SearchEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import com.irsearch.commercesearch.model.ClusterEntity;

public class Cluster {
    HashMap<String, Integer> clusterAssignments;
    HashMap<Integer, String> clusterTitles;
    HashMap<Integer, TreeMap<Double, String>> bestDocuments;

    public Cluster() {
        clusterAssignments = new HashMap<String, Integer>();
        clusterTitles = new HashMap<Integer, String>();
        bestDocuments = new HashMap<Integer, TreeMap<Double, String>>();
    }

    public Object loadModel(String modelFileName) {
        File f = new File(modelFileName);
        Object model = null;

        if (f.exists()) {
            try {
                InputStream is = new FileInputStream(modelFileName);
                ObjectInputStream objectInputStream = new ObjectInputStream(is);
                model = objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                // don't care; will recreate the model.
            }
        }

        return model;
    }

    public void setClusterAssignments(HashMap<String, Integer> clusterAssignments) {
        this.clusterAssignments = clusterAssignments;
    }

    @SuppressWarnings("unchecked")
    public void setClusterAssignments(String fileName) {
        this.clusterAssignments = (HashMap<String, Integer>) loadModel(fileName);
    }

    @SuppressWarnings("unchecked")
    public void setBestDocuments(String fileName) {
        this.bestDocuments = (HashMap<Integer, TreeMap<Double, String>>) loadModel(fileName);
    }

    public void setBestDocuments(HashMap<Integer, TreeMap<Double, String>> bestDocuments) {
        this.bestDocuments = bestDocuments;
    }

    public void setClusterTitles(HashMap<Integer, String> clusterTitles) {
        this.clusterTitles = clusterTitles;
    }

    @SuppressWarnings("unchecked")
    public void setClusterTitles(String fileName) {
        this.clusterTitles = (HashMap<Integer, String>) loadModel(fileName);
    }

    public SearchClusterResults addResults(Vector<String> results) {
        List<SearchEntity> resultList = new ArrayList<SearchEntity>();
        for (String result : results) {
            resultList.add(new SearchEntity(result, "", ""));
        }
        return addResults(resultList);
    }

    public SearchClusterResults addResults(List<SearchEntity> results) {
        TreeMap<Integer, Integer> clusterCounts = new TreeMap<Integer, Integer>();

        Vector<SearchEntity> finalPages = new Vector<SearchEntity>();
        Vector<String> finalUrls = new Vector<String>();

        for (SearchEntity result : results) {
            String url = result.getUrl();
            if (finalUrls.contains(result))
                continue;
            int clusterNum = clusterAssignments.get(url);
            // Count how many results come from each cluster.
            int count = 0;
            if (clusterCounts.containsKey(clusterNum)) {
                count = clusterCounts.get(clusterNum);
            }
            finalPages.add(result);
            finalUrls.add(result.getUrl());
            count++;

            if (bestDocuments != null) {
                TreeMap<Double, String> distances = bestDocuments.get(clusterNum);

                Double bestDistance = distances.firstKey();

                // Quick relevance model; will improve
                for (Map.Entry<Double, String> bestDist : distances.entrySet()) {
                    double ratio = bestDist.getKey() / bestDistance;
                    if (ratio > 0.95
                            && !bestDist.getValue().equalsIgnoreCase(url)
                            && !finalPages.contains(bestDist.getValue())) {
                        finalPages.add(new SearchEntity(bestDist.getValue(), "", ""));
                        finalUrls.add(bestDist.getValue());
                        count++;
                        break;
                    }
                }
            }

            clusterCounts.put(clusterNum, count);
        }

        SearchClusterResults searchClusterResults = new SearchClusterResults();
        List<SearchEntity> searchEntities = new ArrayList<SearchEntity>();
        List<ClusterEntity> clusterEntities = new ArrayList<ClusterEntity>();

        for (SearchEntity page : finalPages) {
            // do a lookup for title and snippet here
            searchEntities.add(page);
        }

        for (int i = 0; i < clusterTitles.size(); i++) {
            if (!clusterTitles.containsKey(i)) continue;

            clusterEntities.add(new ClusterEntity(i,
                    clusterTitles.get(i),
                    clusterCounts.get(i)));
        }

        searchClusterResults.setClusters(clusterEntities);
        searchClusterResults.setResults(searchEntities);

        return searchClusterResults;
    }

}