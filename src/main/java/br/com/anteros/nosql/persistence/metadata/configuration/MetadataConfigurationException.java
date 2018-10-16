package br.com.anteros.nosql.persistence.metadata.configuration;

public class MetadataConfigurationException extends RuntimeException {

	public MetadataConfigurationException() {
	}

	public MetadataConfigurationException(String message) {
		super(message);
	}

	public MetadataConfigurationException(Throwable cause) {
		super(cause);
	}

	public MetadataConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetadataConfigurationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
