package br.com.anteros.nosql.persistence.session.configuration;

import java.io.InputStream;

import br.com.anteros.nosql.persistence.session.configuration.exception.AnterosNoSQLConfigurationException;

public interface BasicConfiguration {

	public BasicConfiguration configure() throws AnterosNoSQLConfigurationException;

	public BasicConfiguration configure(String xmlFile) throws AnterosNoSQLConfigurationException;
	
	public BasicConfiguration configure(InputStream is) throws AnterosNoSQLConfigurationException;
	
}
