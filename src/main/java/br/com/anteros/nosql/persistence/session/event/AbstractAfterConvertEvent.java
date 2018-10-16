package br.com.anteros.nosql.persistence.session.event;

import br.com.anteros.nosql.persistence.session.event.NoSQLEvent;

public abstract class AbstractAfterConvertEvent<T> extends NoSQLEvent<T> {
	
	public AbstractAfterConvertEvent(Object source, T document, String collectionName) {
		super(source, document, collectionName);
	}

}
