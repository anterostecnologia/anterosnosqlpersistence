package br.com.anteros.nosql.persistence.session.handler;

import br.com.anteros.nosql.persistence.session.resultset.NoSQLResultSet;

public interface NoSQLResultSetHandler {
	
	
	public abstract Object handle(NoSQLResultSet resultSet) throws Exception;

}
