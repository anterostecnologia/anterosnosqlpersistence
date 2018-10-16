package br.com.anteros.nosql.persistence.session.impl;

public class NoSQLSessionFactoryException extends RuntimeException {

	public NoSQLSessionFactoryException() {
	}

	public NoSQLSessionFactoryException(String message) {
		super(message);
	}

	public NoSQLSessionFactoryException(Throwable cause) {
		super(cause);
	}

	public NoSQLSessionFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSQLSessionFactoryException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
