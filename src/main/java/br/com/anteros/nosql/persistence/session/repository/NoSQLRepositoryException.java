package br.com.anteros.nosql.persistence.session.repository;

public class NoSQLRepositoryException extends RuntimeException {

	public NoSQLRepositoryException() {
	}

	public NoSQLRepositoryException(String message) {
		super(message);
	}

	public NoSQLRepositoryException(Throwable cause) {
		super(cause);
	}

	public NoSQLRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSQLRepositoryException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
