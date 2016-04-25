package com.irsearch.commercesearch.service.cluster;

import com.irsearch.commercesearch.model.SearchClusterResults;

import com.irsearch.commercesearch.model.SearchEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

import com.irsearch.commercesearch.model.ClusterEntity;
import org.json.JSONArray;
import org.json.JSONObject;

public class Cluster {
    HashMap<String, Double[]> clusterAssignments;
    HashMap<Integer, String> clusterTitles;
    HashMap<Integer, TreeMap<Double, String>> bestDocuments;

    public Cluster() {
        /*
         * clusterAssignments contains the url and a Double[] containing [clusterAssignment, distance]
         */
        clusterAssignments = new HashMap<String, Double[]>();
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

    public void setClusterAssignments(HashMap<String, Double[]> clusterAssignments) {
        this.clusterAssignments = clusterAssignments;
    }

    public void setClusterAssignments(String fileName) {
        this.clusterAssignments = (HashMap<String, Double[]>) loadModel(fileName);
    }

    public void setBestDocuments(String fileName) {
        this.bestDocuments = (HashMap<Integer, TreeMap<Double, String>>) loadModel(fileName);
    }

    public void setBestDocuments(HashMap<Integer, TreeMap<Double, String>> bestDocuments) {
        this.bestDocuments = bestDocuments;
    }

    public void setClusterTitles(HashMap<Integer, String> clusterTitles) {
        this.clusterTitles = clusterTitles;
    }

    public void setClusterTitles(String clusterTitles) {
        this.clusterTitles = (HashMap<Integer, String>) loadModel(clusterTitles);
    }

    public void printClusterTitles() {
        for (Map.Entry<Integer, String> entry: clusterTitles.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
    }

    public void printClusterAssignments() {
        for (Map.Entry<String, Double[]> entry: clusterAssignments.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue()[0] + ", " + entry.getValue()[1]);
        }
    }

    public void printBestDocuments() {
        for (Map.Entry<Integer, TreeMap<Double, String>> entry: bestDocuments.entrySet()) {
            System.out.println("DISTANCES FOR CLUSTER #" + entry.getKey());
            for (Map.Entry<Double, String> entry2: entry.getValue().entrySet()) {
                System.out.println(entry2.getKey() + " => " + entry2.getValue());
            }
        }
    }

    public SearchClusterResults addResults(Vector<String> results) {
        List<SearchEntity> resultList = new ArrayList<SearchEntity>();
        for (String result : results) {
            resultList.add(new SearchEntity(result, "", ""));
        }
        return addResults(resultList);
    }

    public SearchClusterResults addResults(List<SearchEntity> results) {

        List<ClusterEntity> clusterEntities = new ArrayList<>();
        List<SearchEntity> searchEntities = new ArrayList<>();

        HashMap<Integer, Integer> clusterSize = new HashMap<>();

        HashMap<String, Double> newRankings = new HashMap<>();

        double currentRanking = 1.0;
        for (SearchEntity result : results) {
            if (!clusterAssignments.containsKey(result.getUrl())) {
                continue;
            }

            int currentCluster = clusterAssignments.get(result.getUrl())[0].intValue();

            currentRanking = (clusterAssignments.get(result.getUrl())[1] / bestDocuments.get(currentCluster).firstKey()) +
                    ((double)results.size() / currentRanking);

            if (newRankings.containsKey(result.getUrl())) {
                newRankings.put(result.getUrl(), newRankings.get(result.getUrl() + currentRanking));
            } else {
                newRankings.put(result.getUrl(), currentRanking);
            }

            currentRanking++;

            if (clusterSize.containsKey(currentCluster)) {
                clusterSize.put(currentCluster, clusterSize.get(currentCluster) + 1);
                continue;
            }

            TreeMap<Double, String> clusterBest = bestDocuments.get(currentCluster);
            double veryBest = clusterBest.firstKey();


            int docsToAdd = 5;
            for (Map.Entry<Double, String> topDocs : clusterBest.entrySet()) {
                if (newRankings.containsKey(topDocs.getValue())) {
                    newRankings.put(topDocs.getValue(), newRankings.get(topDocs.getValue())
                            + (topDocs.getKey() / veryBest));
                } else {
                    newRankings.put(topDocs.getValue(), (topDocs.getKey() / veryBest));
                }
                docsToAdd--;

                if (docsToAdd < 0) {
                    break;
                }
            }

            ClusterEntity clusterEntity = new ClusterEntity();
            clusterEntity.setTitle(clusterTitles.get(currentCluster));
            clusterEntity.setClusterNo(currentCluster);
            clusterEntities.add(clusterEntity);
            clusterSize.put(currentCluster, 1);

        }


        TreeMap<Double, SearchEntity> rankedResults = new TreeMap<>(Collections.reverseOrder());

        for (SearchEntity result : results) {
            rankedResults.put(newRankings.get(result.getUrl()), result);
            newRankings.remove(result.getUrl());
        }

        for (Map.Entry<String, Double> entry : newRankings.entrySet()) {
            // Ram - need to do lookup here
            rankedResults.put(entry.getValue(), new SearchEntity(entry.getKey(), entry.getValue().toString(), ""));
        }

        List<SearchEntity> finalResults = new ArrayList<>(rankedResults.values());

//        for (ClusterEntity clusterEntity : clusterEntities) {
//            clusterEntity.setSize(clusterSize.get(clusterEntity.getClusterNo()));
//        }
//
//        HashMap<String, Double> newRanks = new HashMap<>();
//        HashMap<String, SearchEntity> searchEntityHashMap = new HashMap<>();
//
//        HashMap<Integer, Integer> clustersInResults = new HashMap<>();
//
//        double originalSize = (double) results.size();

        SearchClusterResults searchClusterResults = new SearchClusterResults();

        searchClusterResults.setClusters(clusterEntities);
        searchClusterResults.setResults(finalResults);
        searchClusterResults.setResultCount(finalResults.size());

        searchClusterResults.setClusters(clusterEntities);

        return searchClusterResults;
// http://bl.ocks.org/mbostock/4063269
//        double rankCount = 1;
//        double additionalItems = results.size() / 4.0;
//
//        for (SearchEntity result : results) {
//
//            searchEntityHashMap.put(result.getUrl(), result);
//            /*
//             * We want an initial rank score to be based on an ratio of the total result set size
//             * for the returned results (~200) over the document's original rank in that result set.
//             */
//            double rankAdd = (originalSize / rankCount);
//
//            /*
//             * If result is in newRanks, it was added for being close to a centroid
//             * and we want to add the initial rank score to the existing score.
//             */
//            if (newRanks.containsKey(result.getUrl())) {
//                rankAdd += newRanks.get(result.getUrl()) + (originalSize / rankCount);
//            }
//
//            /*
//             * If for some reason the url wasn't clustered, continue.
//             */
//            if (!clusterAssignments.containsKey(result.getUrl())) {
//                continue;
//            }
//
//            int assignment = clusterAssignments.get(result.getUrl())[0].intValue();
//
//            /*
//             * We want the cluster score for the document to be equal to the
//             * the best document (closest to the centroid)'s distance over the current url's distance.
//             */
//            rankAdd += (
//                    bestDocuments.get(assignment).firstKey() /
//                            clusterAssignments.get(result.getUrl())[1]
//            );
//            newRanks.put(result.getUrl(), rankAdd);
//
//
//            /*
//             * We want to count how many times each cluster shows up.
//             */
//            if (!clustersInResults.containsKey(assignment)) {
//                clustersInResults.put(assignment, 0);
//            }
//            clustersInResults.put(assignment, clustersInResults.get(assignment) + 1);
//
//            if (!newRanks.containsKey(bestDocuments.get(assignment).firstEntry().getValue())) {
//                newRanks.put(bestDocuments.get(assignment).firstEntry().getValue(), 1.0);
//            }
//
//            newRanks.put(bestDocuments.get(assignment).firstEntry().getValue(),
//                    newRanks.get(bestDocuments.get(assignment).firstEntry().getValue()) + rankAdd );
//
//
//            rankCount++;
//        }
//
//        System.out.println("Items ranked");
//
//        /*
//         * Now we want to add X additional documents
//         */
//        for (Map.Entry<Integer, Integer> counts : clustersInResults.entrySet()) {
//            int cluster = counts.getKey();
//            double bestDistance = bestDocuments.get(cluster).firstKey();
//            double total = (double) counts.getValue();
//
//            int docsToAdd = (int)(((total / originalSize) * additionalItems) + 0.5);
//
//            for (Map.Entry<Double, String> newDocs : bestDocuments.get(cluster).entrySet()) {
//                if (!newRanks.containsKey(newDocs.getValue())) {
//                    newRanks.put(newDocs.getValue(), 0.0);
//                }
//
//                newRanks.put(newDocs.getValue(),
//                        newRanks.get(newDocs.getValue()) + total * (newDocs.getKey() / bestDistance));
//            }
//        }
//
//
//
//        /*
//         * Now to reorder them.
//         */
//        TreeMap<Double, SearchEntity> finalRankings = new TreeMap<Double, SearchEntity>();
//        for (Map.Entry<String, Double> newRank : newRanks.entrySet()) {
//            SearchEntity searchEntity = new SearchEntity();
//            if (searchEntityHashMap.containsKey(newRank.getKey())) {
//                // One of the original items, so we have the result.
//                searchEntity = searchEntityHashMap.get(newRank.getKey());
//            } else {
//                // New result; look for result in index.  Need to add lookup
//                searchEntity.setUrl(newRank.getKey());
//            }
//
//            finalRankings.put(newRank.getValue(), searchEntity);
//        }
//
//        SearchClusterResults searchClusterResults = new SearchClusterResults();
//        List<SearchEntity> searchEntities = new ArrayList<SearchEntity>(finalRankings.values());
//        List<ClusterEntity> clusterEntities = new ArrayList<ClusterEntity>();
//
//        for (int i = 0; i < clusterTitles.size(); i++) {
//            if (!clusterTitles.containsKey(i)) continue;
//
//            clusterEntities.add(new ClusterEntity(i,
//                    clusterTitles.get(i),
//                    clustersInResults.get(i)));
//        }
//
//        searchClusterResults.setClusters(clusterEntities);
//        searchClusterResults.setResults(searchEntities);
//
//        System.out.println("Results returned");
//        return searchClusterResults;
    }

    public void generateGraphJSONAll() {
        int test = 0;
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, String> entry: clusterTitles.entrySet()) {
            JSONArray ja = new JSONArray();
            JSONObject root = new JSONObject();
            root.put("name", entry.getValue());
            root.put("size", bestDocuments.get(entry.getKey()).size());
            ja.put(root);

            TreeMap<Double, String> documents = bestDocuments.get(entry.getKey());
            JSONArray children = new JSONArray();
            int test2 = 0;
            for (Map.Entry<Double, String> docEntry : documents.entrySet()) {
                JSONObject child = new JSONObject();
                child.put("name", docEntry.getValue());
                child.put("size", docEntry.getKey());
                children.put(child);
                test2++;
                if (test2 > 1) {
                    break;
                }
            }
            JSONObject temp = new JSONObject();
            temp.put("children", children);
            ja.put(temp);
            System.out.println(ja.toString());
            jsonArray.put(ja);
            test++;

            if (test > 1) {
                break;
            }
        }

        ClusterFileUtil.saveModel("testingJSON.json", jsonArray.toString());
        System.out.println(jsonArray.toString());

    }


}