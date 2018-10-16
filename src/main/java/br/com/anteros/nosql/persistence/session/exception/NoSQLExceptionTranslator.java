package br.com.anteros.nosql.persistence.session.exception;

public interface NoSQLExceptionTranslator {
	
		NoSQLDataAccessException translateExceptionIfPossible(Exception ex);

}
