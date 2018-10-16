package br.com.anteros.nosql.persistence.session.handler;

import br.com.anteros.nosql.persistence.session.resultset.NoSQLResultSet;

public class NoSQLEntityHandler implements NoSQLScrollableResultSetHandler {

	@Override
	public Object handle(NoSQLResultSet resultSet) throws NoSQLResultSetHandlerException {
		return null;
	}

	@Override
	public Object[] readCurrentRow(NoSQLResultSet resultSet) throws Exception {
		return null;
	}
	
}