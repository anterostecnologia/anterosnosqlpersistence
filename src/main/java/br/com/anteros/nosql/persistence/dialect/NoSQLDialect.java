package br.com.anteros.nosql.persistence.dialect;

import java.lang.reflect.Type;

import br.com.anteros.nosql.persistence.client.NoSQLDataSourceBuilder;
import br.com.anteros.nosql.persistence.client.NoSQLSessionBuilder;
import br.com.anteros.nosql.persistence.converters.NoSQLConverters;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionFactory;

public abstract class NoSQLDialect {
	
	public abstract NoSQLTransactionFactory getTransactionFactory();
	
	public abstract NoSQLDataSourceBuilder getDataSourceBuilder() throws Exception;
	
	public abstract NoSQLSessionBuilder getSessionBuilder() throws Exception;

	public abstract NoSQLConverters getDefaultConverters(AbstractNoSQLObjectMapper mapper);

	public abstract Object getDbObjectValue(String fieldName, Object noSQLObject, boolean isIdentifier);

	public abstract void setDbObjectValue(Object noSQLObject, String name, Object encoded, boolean isIdentifier);
	
	public abstract boolean isDatabaseType(Class<?> type);
	
	public abstract boolean isDatabaseType(Type type);

	public abstract Class<?> getSessionInterface();
	
}
