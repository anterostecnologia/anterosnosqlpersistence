package br.com.anteros.nosql.persistence.session.event;

public abstract class AbstractBeforeDeleteEvent<T> extends NoSQLEvent<T> {

	public AbstractBeforeDeleteEvent(T document, Class<?> type, String collectionName) {
		super(type, document, collectionName);
	}
}
