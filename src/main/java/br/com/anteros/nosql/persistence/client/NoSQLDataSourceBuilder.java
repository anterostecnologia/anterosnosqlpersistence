package br.com.anteros.nosql.persistence.client;

import java.util.Properties;

import br.com.anteros.nosql.persistence.session.configuration.DataSourceConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.PropertiesConfiguration;



public interface NoSQLDataSourceBuilder {

	public NoSQLDataSourceBuilder configure(DataSourceConfiguration configuration);

	public NoSQLDataSourceBuilder configure(Properties props);
	
	public NoSQLDataSourceBuilder configure(PropertiesConfiguration props);

	public NoSQLDataSource build();

}
