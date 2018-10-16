package br.com.anteros.nosql.persistence.session.configuration;

import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;


public interface NoSQLPersistenceConfiguration extends BasicConfiguration {

	public NoSQLSessionFactory buildSessionFactory() throws Exception;

}
