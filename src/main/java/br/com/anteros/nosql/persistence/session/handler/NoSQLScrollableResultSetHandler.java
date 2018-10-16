package br.com.anteros.nosql.persistence.session.handler;

import br.com.anteros.nosql.persistence.session.resultset.NoSQLResultSet;

public interface NoSQLScrollableResultSetHandler extends NoSQLResultSetHandler {

	/**
	 * Método responsável por ler a linha corrente do NoSQLResultSet.
	 * 
	 */
	public abstract Object[] readCurrentRow(NoSQLResultSet resultSet) throws Exception;
}