package br.com.anteros.nosql.persistence.session.exception;

public abstract class NoSQLDataAccessException extends NoSQLNestedRuntimeException {

	public NoSQLDataAccessException(String msg) {
		super(msg);
	}

	public NoSQLDataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
