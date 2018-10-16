package br.com.anteros.nosql.persistence.client;

public interface NoSQLConnection {

	boolean isClosed();

	void close();

}
