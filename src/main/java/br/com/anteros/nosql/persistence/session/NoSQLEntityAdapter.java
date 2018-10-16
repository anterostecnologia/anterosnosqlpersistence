package br.com.anteros.nosql.persistence.session;

import br.com.anteros.nosql.persistence.session.query.NoSQLQuery;

public interface NoSQLEntityAdapter<T> {

	String getIdFieldName();

	Object getId();

	<Q> NoSQLQuery<Q> getByIdQuery();

	<Q> NoSQLQuery<Q> getQueryForVersion();

	NoSQLMappedDocument toMappedDocument();

	default void assertUpdateableIdIfNotSet() {
	}

	default boolean isVersionedEntity() {
		return false;
	}

	Object getVersion();

	T getEntity();

	T populateIdIfNecessary(Object id);

	T initializeVersionProperty();

	T incrementVersion();

}
