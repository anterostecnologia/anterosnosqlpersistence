package br.com.anteros.nosql.persistence.session.event;

public abstract class AbstractAfterDeleteEvent<T> extends NoSQLEvent<T> {

	public AbstractAfterDeleteEvent(T document, Class<?> type, String collectionName) {
		super(type, document, collectionName);
	}
}
