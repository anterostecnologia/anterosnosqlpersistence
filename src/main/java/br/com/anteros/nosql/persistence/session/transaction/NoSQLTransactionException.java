package br.com.anteros.nosql.persistence.session.transaction;

public class NoSQLTransactionException extends RuntimeException {

	public NoSQLTransactionException() {
	}

	public NoSQLTransactionException(String message) {
		super(message);
	}

	public NoSQLTransactionException(Throwable cause) {
		super(cause);
	}

	public NoSQLTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSQLTransactionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
