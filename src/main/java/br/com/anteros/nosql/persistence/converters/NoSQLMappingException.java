package br.com.anteros.nosql.persistence.converters;

public class NoSQLMappingException extends RuntimeException {

	public NoSQLMappingException() {
		super();
	}

	public NoSQLMappingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoSQLMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSQLMappingException(String message) {
		super(message);
	}

	public NoSQLMappingException(Throwable cause) {
		super(cause);
	}

}
