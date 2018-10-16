package br.com.anteros.nosql.persistence.session.transaction;

public interface NoSQLTransaction {

	public void begin();
	
	public void begin(NoSQLTransactionOptions options);

	public void commit() throws Exception;

	public void rollback();

	public boolean isActive();

	public String debugString();
	
	public void registerSynchronization(NoSQLTransactionSynchronization synchronization);

	public void close();

}
