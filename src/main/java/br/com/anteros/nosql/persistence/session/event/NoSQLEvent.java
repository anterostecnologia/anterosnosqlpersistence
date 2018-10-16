package br.com.anteros.nosql.persistence.session.event;

import br.com.anteros.core.utils.Assert;

public abstract class NoSQLEvent<T>{

	private final long timestamp;
	private final T document;
	private final String collectionName;
	private final Object source;
	private Class<?> type;


	public NoSQLEvent(Object source, T document, String collectionName) {
		Assert.notNull(source, "Source must not be null!");
		this.source = source;
		this.document = document;
		this.collectionName = collectionName;
		this.timestamp = System.currentTimeMillis();
	}
	
	public NoSQLEvent(Class<?> type, T document, String collectionName) {
		Assert.notNull(type, "Type must not be null!");
		this.type = type;
		this.document = document;
		this.collectionName = collectionName;
		this.timestamp = System.currentTimeMillis();
		this.source = null;
	}


	public T getDocument() {
		return document;
	}
	
	public Object getSource() {
		return source;
	}

	public String getCollectionName() {
		return collectionName;
	}


	public final long getTimestamp() {
		return this.timestamp;
	}

	public Class<?> getType() {
		return type;
	}

}
