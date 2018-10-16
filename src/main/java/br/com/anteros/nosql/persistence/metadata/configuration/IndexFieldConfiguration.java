package br.com.anteros.nosql.persistence.metadata.configuration;

import br.com.anteros.nosql.persistence.utils.IndexType;

public class IndexFieldConfiguration {
	
	private IndexType indexType = IndexType.ASC;
    private String name;
    private int weight = -1;
    
    public IndexFieldConfiguration(String name) {
    	this.name=name;
    }
    
    public IndexFieldConfiguration indexType(IndexType indexType) {
    	this.indexType = indexType;
    	return this;
    }
    
    public IndexFieldConfiguration weight(int weight) {
    	this.weight = weight;
    	return this;
    }

	public IndexType getIndexType() {
		return indexType;
	}

	public String getName() {
		return name;
	}

	public int getWeight() {
		return weight;
	}
}
