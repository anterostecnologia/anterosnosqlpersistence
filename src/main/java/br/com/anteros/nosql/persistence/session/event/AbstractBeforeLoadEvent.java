package br.com.anteros.nosql.persistence.session.event;

import br.com.anteros.nosql.persistence.session.event.NoSQLEvent;

public abstract class AbstractBeforeLoadEvent<T> extends NoSQLEvent<T> {

	public AbstractBeforeLoadEvent(Object source, T document, String collectionName) {
		super(source, document, collectionName);
	}

}
