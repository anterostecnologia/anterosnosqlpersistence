package br.com.anteros.nosql.persistence.session.event;

import br.com.anteros.nosql.persistence.session.event.NoSQLEvent;

public abstract class AbstractAfterSaveEvent<T> extends NoSQLEvent<T> {

	public AbstractAfterSaveEvent(Object source, T document, String collectionName) {
		super(source, document, collectionName);
	}

}
