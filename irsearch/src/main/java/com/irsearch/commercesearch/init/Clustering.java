package com.irsearch.commercesearch.init;

import org.json.JSONArray;

import com.irsearch.commercesearch.service.cluster.Cluster;
import com.irsearch.commercesearch.service.cluster.ClusterFileUtil;

import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;

public class Clustering {
	//TODO:change to proper location
	public static String DATA_FILE = "./current.arff";
	//TODO:change to proper location
	public static final String WEB_PAGES = "./somepages.dat";

	public static void main(String[] args) {
		// Only cluster code needed in application
		Cluster cluster = new Cluster();

		// Set cluster from java object files
		cluster.setBestDocuments("./bestDocuments");
		cluster.setClusterAssignments("./clusterAssignments");
		cluster.setClusterTitles("./clusterTitles");

		// This takes in a vector<string> or List<Result> and return a JSONObject with additional pages
		Vector<String> testPages = new Vector<String>();
		testPages.add("http://www.target.com/p/convenience-concepts-northfield-wall-console-table/-/A-10974416");
		testPages.add("http://www.target.com/p/threshold-parsons-coffee-table/-/A-16982954");
		testPages.add("http://www.target.com/tdir/p/beauty/-/N-55r1x");
		//JSONArray json = cluster.addResults(testPages);  // get this results from Ram

//		ClusterFileUtil.breakOutFile("./somepages.dat");
		//ClusterFileUtil.readWebpages("./somepages.dat", "./testingDat");
//
//		HashMap<String, Integer> testTreeMap = new HashMap<String, Integer>();
//
//		for (int ii = 0; ii < 200000; ii++) {
//			testTreeMap.put("reallylongasdfkjas;dlfkjstringnamewithstuff" + ii, ii);
//		}
//
//		ClusterFileUtil.saveModel("./hashMapModel", testTreeMap);

	}

	public static void main2(String[] args) throws Throwable {
		
		if (args.length > 1) {
			DATA_FILE = args[0];
		}

		DataSource source = ClusterFileUtil.getDataSourceFromFile(WEB_PAGES);

		Instances data = ClusterFileUtil.getData(source, true);
		
		
		if (data == null) {
			System.out.println("Problem getting data.  Returning.");
		}

		// Clusterers to be used.
		// Simple k-means: http://weka.sourceforge.net/doc.dev/weka/clusterers/SimpleKMeans.html
		System.out.println("Creating SimpleKMeans");
		SimpleKMeans kMeans = ClusteringUtils.newKMeans(data, "-N 10 -O", "kMeans.model", false);
		//kMeans = null;

		System.out.println("Creating hc1");
		HierarchicalClusterer hc1 = ClusteringUtils.newHier(data, "-N 100 -L COMPLETE -A weka.core.EuclideanDistance", "hier12_12.model");
		System.out.println("Creating hc2");
		HierarchicalClusterer hc2 = ClusteringUtils.newHier(data, "-N 100 -L SINGLE -A weka.core.EuclideanDistance", "hier2.model");
		System.out.println("Creating hc3");
		HierarchicalClusterer hc3 = ClusteringUtils.newHier(data, "-N 100 -L CENTROID -A weka.core.EuclideanDistance", "hier3.model");
		System.out.println("Creating hc4");
		HierarchicalClusterer hc4 = ClusteringUtils.newHier(data, "-N 100 -L NEIGHBOR_JOINING -A weka.core.EuclideanDistance", "single_hier.model");

		int i=0;
		HashMap<String, Integer> kMeansAssignments = new HashMap<String, Integer>();
		HashMap<Integer, TreeMap<Double, String>> centralPages = new HashMap<Integer, TreeMap<Double, String>>();
		Instances data2 = ClusterFileUtil.getData(source,  false);
		int[] assignments = kMeans.getAssignments();

		EuclideanDistance ed = (EuclideanDistance)kMeans.getDistanceFunction();
		
		for(int clusterNum : assignments) {
			kMeansAssignments.put(data2.instance(i).toString(1), clusterNum);
			
			if (!centralPages.containsKey(clusterNum)) {
				centralPages.put(clusterNum, new TreeMap<Double, String>());
			}
			
			TreeMap<Double, String> temp = centralPages.get(clusterNum);
			temp.put(ed.distance(data.instance(i), kMeans.getClusterCentroids().instance(clusterNum)), data2.instance(i).toString(1));
			
			centralPages.put(clusterNum, temp);
			
		    i++;
		}
		
		for (Map.Entry<Integer, TreeMap<Double, String>> clusters : centralPages.entrySet()) {
			System.out.println("BEST FOR CLUSTER #" + clusters.getKey());
			for (Map.Entry<Double, String> pages : clusters.getValue().entrySet()) {
				System.out.println(pages.getKey() + " => " + pages.getValue());
			}
		}
		
		ClusterFileUtil.saveModel("./bestDocuments", centralPages);
		
		for (Map.Entry<String, Integer> entry : kMeansAssignments.entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue());
		}
		
		ClusterFileUtil.saveModel("./clusterAssignments", kMeansAssignments);
		
		System.out.println("Done");


		Vector<String> testPages = new Vector<String>();
		testPages.add("http://www.target.com/p/convenience-concepts-northfield-wall-console-table/-/A-10974416");
		testPages.add("http://www.target.com/p/threshold-parsons-coffee-table/-/A-16982954");
		testPages.add("http://www.target.com/tdir/p/beauty/-/N-55r1x");
		
		TreeMap<Integer, Integer> clusterCounts = new TreeMap<Integer, Integer>();
		
		Vector<String> finalPages = new Vector<String>();
		
		for (String url : testPages) {
			if (finalPages.contains(url))
				continue;
			int clusterNum = kMeansAssignments.get(url);
			// Count how many results come from each cluster.
			int count = 0;
			if (clusterCounts.containsKey(clusterNum)) {
				count = clusterCounts.get(clusterNum);
			}
			finalPages.add(url);
			count++;
			
			TreeMap<Double, String> distances = centralPages.get(clusterNum);
			
			Double bestDistance = distances.firstKey();

			for (Map.Entry<Double, String> bestDist : distances.entrySet()) {
				double ratio = bestDist.getKey() / bestDistance;
				if (ratio > 0.95 && !bestDist.getValue().equalsIgnoreCase(url) && !finalPages.contains(bestDist.getValue())) {
					finalPages.add(bestDist.getValue());
					count++;
					break;
				}
			}
			
			clusterCounts.put(clusterNum, count);
		}
		
		ClusterFileUtil.produceJSON(kMeans, finalPages, clusterCounts);
		
		//System.out.println(kMeans.toString());
		
		//ClusteringUtils.produceKMeansLabels(kMeans, 10);
		
		ClusterFileUtil.produceJsonKMeansClusters(kMeans, "./test2.json", 10);
		
//		kMeansAssignments = (TreeMap<String, Integer>) ClusterFileUtil.tryToLoadModel("./kMeansTreeMap");

		HashMap<String, Integer> testTreeMap = new HashMap<String, Integer>();

		for (int ii = 0; ii < 200000; ii++) {
			testTreeMap.put("reallylongstringnamewithstuff" + ii, ii);
		}

		ClusterFileUtil.saveModel("./clusterTitles", ClusterFileUtil.getClusterTitles(kMeans));
		ClusterFileUtil.saveModel("./hashMapModel", testTreeMap);


	}
}
