package br.com.anteros.nosql.persistence.session.event;

import br.com.anteros.nosql.persistence.session.event.NoSQLEvent;

public abstract class AbstractBeforeSaveEvent<T> extends NoSQLEvent<T> {

	public AbstractBeforeSaveEvent(Object source, T document, String collectionName) {
		super(source, document, collectionName);
	}

}
