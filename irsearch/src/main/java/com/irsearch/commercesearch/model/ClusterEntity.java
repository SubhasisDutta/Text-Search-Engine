package com.irsearch.commercesearch.model;

import java.util.List;

public class ClusterEntity {	
	private String title;
	private int size;
	private int clusterNo;
	private List<SearchEntity> nodes;	// not sure if neded
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getClusterNo() {
		return clusterNo;
	}
	public void setClusterNo(int clusterNo) {
		this.clusterNo = clusterNo;
	}
	public List<SearchEntity> getNodes() {
		return nodes;
	}
	public void setNodes(List<SearchEntity> nodes) {
		this.nodes = nodes;
	}
	
	//TODO: Wyatt add the attributes u think is relevant

	
	
	
	
	
	
}
