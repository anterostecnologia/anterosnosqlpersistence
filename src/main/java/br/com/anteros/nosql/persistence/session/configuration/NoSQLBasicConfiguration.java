package br.com.anteros.nosql.persistence.session.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import br.com.anteros.core.configuration.AnterosCoreProperties;
import br.com.anteros.core.utils.ResourceUtils;
import br.com.anteros.nosql.persistence.session.configuration.exception.AnterosNoSQLConfigurationException;



public abstract class NoSQLBasicConfiguration implements BasicConfiguration {

	protected NoSQLSessionFactoryConfiguration sessionFactory;

	public NoSQLSessionFactoryConfiguration getSessionFactoryConfiguration() {
		if (sessionFactory == null)
			sessionFactory = new NoSQLSessionFactoryConfiguration();
		return sessionFactory;
	}

	public void setSessionFactory(NoSQLSessionFactoryConfiguration value) {
		this.sessionFactory = value;
	}

	public NoSQLBasicConfiguration addAnnotatedClass(Class<?> clazz) {
		getSessionFactoryConfiguration().getAnnotatedClasses().getClazz().add(clazz.getName());
		return this;
	}

	public NoSQLBasicConfiguration addAnnotatedClass(String clazz) {
		getSessionFactoryConfiguration().getAnnotatedClasses().getClazz().add(clazz);
		return this;
	}

	public NoSQLBasicConfiguration setLocationPlaceHolder(String location) {
		getSessionFactoryConfiguration().getPlaceholder().setLocation(location);
		return this;
	}

	public NoSQLBasicConfiguration addDataSource(DataSourceConfiguration dataSource) {
		getSessionFactoryConfiguration().getDataSources().getDataSources().add(dataSource);
		return this;
	}

	public NoSQLBasicConfiguration addDataSource(String id, Class<?> clazz, PropertyConfiguration[] properties) {
		return addDataSource(id, clazz.getName(), properties);
	}

	public NoSQLBasicConfiguration addDataSource(String id, String clazz, PropertyConfiguration[] properties) {
		DataSourceConfiguration dataSource = new DataSourceConfiguration(id, clazz);
		for (PropertyConfiguration propertyConfiguration : properties) {
			dataSource.getProperties().add(propertyConfiguration);
		}
		getSessionFactoryConfiguration().getDataSources().getDataSources().add(dataSource);
		return this;
	}

	public NoSQLBasicConfiguration addDataSource(String id, Class<?> clazz, Map<String, String> properties) {
		return addDataSource(id, clazz.getName(), properties);
	}

	public NoSQLBasicConfiguration addDataSource(String id, String clazz, Map<String, String> properties) {
		List<PropertyConfiguration> props = new ArrayList<PropertyConfiguration>();
		for (String property : properties.keySet()) {
			props.add(new PropertyConfiguration().setName(property).setValue(properties.get(property)));
		}
		return addDataSource(id, clazz, props.toArray(new PropertyConfiguration[] {}));
	}

	public NoSQLBasicConfiguration addDataSource(String id, Class<?> clazz, Properties properties) {
		return addDataSource(id, clazz.getName(), properties);
	}

	public NoSQLBasicConfiguration addDataSource(String id, String clazz, Properties properties) {
		List<PropertyConfiguration> props = new ArrayList<PropertyConfiguration>();
		for (Object property : properties.keySet()) {
			props.add(new PropertyConfiguration().setName((String) property)
					.setValue((String) properties.get(property)));
		}
		return addDataSource(id, clazz, props.toArray(new PropertyConfiguration[] {}));
	}

	public NoSQLBasicConfiguration addProperty(PropertyConfiguration property) {
		getSessionFactoryConfiguration().getProperties().getProperties().add(property);
		return this;
	}

	public NoSQLBasicConfiguration addProperties(Properties properties) {
		for (Object property : properties.keySet()) {
			addProperty(new PropertyConfiguration().setName((String) property).setValue(
					(String) properties.get(property)));
		}
		return this;
	}

	public NoSQLBasicConfiguration addProperties(PropertyConfiguration[] properties) {
		for (PropertyConfiguration property : properties) {
			addProperty(property);
		}
		return this;
	}

	public NoSQLBasicConfiguration addProperty(String name, String value) {
		addProperty(new PropertyConfiguration().setName(name).setValue(value));
		return this;
	}

	public NoSQLBasicConfiguration configure() throws AnterosNoSQLConfigurationException {
		return configure(AnterosCoreProperties.XML_CONFIGURATION);
	}

	public NoSQLBasicConfiguration configure(String xmlFile) throws AnterosNoSQLConfigurationException {
		InputStream is=null;
		try {
			final List<URL> resources = ResourceUtils.getResources(xmlFile, getClass());
			if ((resources != null) && (resources.size() > 0)) {
				final URL url = resources.get(0);
				is = url.openStream();
				configure(is);
				is.close();
				return this;
			}
		} catch (final Exception e) {
			if (is!=null) {
				try {
					is.close();
				} catch (IOException e1) {
				}
			}
			throw new AnterosNoSQLConfigurationException("Impossível realizar a leitura " + xmlFile + " " + e);
		}

		throw new AnterosNoSQLConfigurationException("Arquivo de configuração " + xmlFile + " não encontrado.");
	}

	public NoSQLBasicConfiguration configure(InputStream xmlConfiguration) throws AnterosNoSQLConfigurationException {
		try {
			final NoSQLBasicConfiguration result = parseXmlConfiguration(xmlConfiguration);
			this.setSessionFactory(result.getSessionFactoryConfiguration());
			return this;
		} catch (final Exception e) {
			throw new AnterosNoSQLConfigurationException("Impossível realizar a leitura do arquivo de configuração." + e);
		}
	}

	public NoSQLBasicConfiguration configure(InputStream xmlConfiguration, InputStream placeHolder)
			throws AnterosNoSQLConfigurationException {
		try {
			final NoSQLBasicConfiguration result = parseXmlConfiguration(xmlConfiguration);
			result.setPlaceHolder(placeHolder);
			this.setSessionFactory(result.getSessionFactoryConfiguration());

			return this;
		} catch (final Exception e) {
			throw new AnterosNoSQLConfigurationException("Impossível realizar a leitura do arquivo de configuração." + e);
		}
	}
	
	protected abstract NoSQLBasicConfiguration parseXmlConfiguration(InputStream xmlConfiguration) throws Exception;

	public String getProperty(String name) {
		return getSessionFactoryConfiguration().getProperties().getProperty(name);
	}

	public NoSQLBasicConfiguration setPlaceHolder(InputStream placeHolder) throws IOException {
		if (placeHolder != null) {
			Properties props = new Properties();
			props.load(placeHolder);
			getSessionFactoryConfiguration().getPlaceholder().setProperties(props);
		}
		return this;
	}

	public NoSQLBasicConfiguration setProperties(Properties props) {
		getSessionFactoryConfiguration().getProperties().setProperties(props);
		return this;
	}

	public static InputStream getDefaultXmlInputStream() throws Exception {
		List<URL> resources = ResourceUtils.getResources(AnterosCoreProperties.XML_CONFIGURATION,
				NoSQLBasicConfiguration.class);
		if ((resources == null) || (resources.isEmpty())) {
			resources = ResourceUtils.getResources("/assets" + AnterosCoreProperties.XML_CONFIGURATION,
					NoSQLBasicConfiguration.class);
			if (resources == null || resources.isEmpty()) {
				return null;
			}
		}
		final URL url = resources.get(0);
		return url.openStream();
	}

}
