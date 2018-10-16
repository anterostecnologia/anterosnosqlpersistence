package br.com.anteros.nosql.persistence.session.transaction;

public interface NoSQLTransactionSynchronization {

	public void beforeCompletion();

	public void afterCompletion(int status);

}
