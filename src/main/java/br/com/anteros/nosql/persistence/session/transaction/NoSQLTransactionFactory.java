package br.com.anteros.nosql.persistence.session.transaction;

import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.session.NoSQLPersistenceContext;

public interface NoSQLTransactionFactory {


	public NoSQLTransaction createTransaction(NoSQLConnection connection, NoSQLPersistenceContext context);

}
