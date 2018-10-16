package br.com.anteros.nosql.persistence.session.event;

public abstract class AbstractAfterLoadEvent<T> extends NoSQLEvent<T> {

	public AbstractAfterLoadEvent(T document, Class<?> type, String collectionName) {
		super(type, document, collectionName);
	}
}
