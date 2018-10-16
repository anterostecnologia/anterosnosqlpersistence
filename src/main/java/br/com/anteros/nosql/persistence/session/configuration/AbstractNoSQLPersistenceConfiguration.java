/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package br.com.anteros.nosql.persistence.session.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import br.com.anteros.core.configuration.AnterosCoreProperties;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.scanner.ClassFilter;
import br.com.anteros.core.scanner.ClassPathScanner;
import br.com.anteros.core.utils.ResourceUtils;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.nosql.persistence.client.NoSQLDataSource;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.metadata.accessor.PropertyAccessorFactory;
import br.com.anteros.nosql.persistence.metadata.annotations.Converters;
import br.com.anteros.nosql.persistence.metadata.annotations.Embedded;
import br.com.anteros.nosql.persistence.metadata.annotations.Entity;
import br.com.anteros.nosql.persistence.metadata.annotations.EnumValues;
import br.com.anteros.nosql.persistence.metadata.comparator.DependencyComparator;
import br.com.anteros.nosql.persistence.metadata.configuration.NoSQLPersistenceModelConfiguration;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.configuration.exception.AnterosNoSQLConfigurationException;
import br.com.anteros.nosql.persistence.session.configuration.impl.AnterosNoSQLPersistenceConfiguration;



public abstract class AbstractNoSQLPersistenceConfiguration extends NoSQLBasicConfiguration implements NoSQLPersistenceConfiguration {

	protected static Logger LOG = LoggerProvider.getInstance().getLogger(AbstractNoSQLPersistenceConfiguration.class);

	public static final String SECURITY_PACKAGE = "br.com.anteros.security.model";
	public static final String CONVERTERS_PACKAGE = "br.com.anteros.persistence.nosql.converters";

	public static final String ANNOTATED_CLASSES = "annotatedClasses";

	public static final String PROPERTIES = "properties/property";

	public static final String LOCATION = "location";

	public static final String ID = "id";

	public static final String CLASS_NAME = "className";

	public static final String VALUE = "value";

	public static final String NAME = "name";

	public static final String PROPERTY = "property";

	public static final String DATA_SOURCE_PROPERTY = "dataSource/property";

	public static final String INCLUDE_SECURITY_MODEL = "include-security-model";

	public static final String DATA_SOURCES = "dataSources/dataSource";

	public static final String DATA_SOURCE = "dataSource";

	public static final String PACKAGE_NAME = "package-name";

	public static final String PACKAGE_SCAN_ENTITY = "package-scan-entity";

	public static final String PLACEHOLDER = "placeholder";

	public static String SESSION_FACTORY_PATH = "anteros-configuration/session-factory";

	protected NoSQLDescriptionEntityManager descriptionEntityManager;
	protected NoSQLDataSource dataSource;
	protected NoSQLPersistenceModelConfiguration modelConfiguration;

	public AbstractNoSQLPersistenceConfiguration() {
		descriptionEntityManager = new NoSQLDescriptionEntityManager();
	}

	public AbstractNoSQLPersistenceConfiguration(NoSQLDataSource dataSource) {
		this();
		this.dataSource = dataSource;
	}

	public AbstractNoSQLPersistenceConfiguration(NoSQLPersistenceModelConfiguration modelConfiguration) {
		this();
		this.modelConfiguration = modelConfiguration;
	}

	public AbstractNoSQLPersistenceConfiguration(NoSQLDataSource dataSource, NoSQLPersistenceModelConfiguration modelConfiguration) {
		super();
		this.dataSource = dataSource;
		this.modelConfiguration = modelConfiguration;
	}

	public NoSQLSessionFactoryConfiguration getSessionFactoryConfiguration() {
		if (sessionFactory == null)
			sessionFactory = new NoSQLSessionFactoryConfiguration();
		return sessionFactory;
	}

	public void setSessionFactory(NoSQLSessionFactoryConfiguration value) {
		this.sessionFactory = value;
	}

	public AbstractNoSQLPersistenceConfiguration addAnnotatedClass(Class<?> clazz) {
		getSessionFactoryConfiguration().getAnnotatedClasses().getClazz().add(clazz.getName());
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration addAnnotatedClass(String clazz) {
		getSessionFactoryConfiguration().getAnnotatedClasses().getClazz().add(clazz);
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration setLocationPlaceHolder(String location) {
		getSessionFactoryConfiguration().getPlaceholder().setLocation(location);
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration addDataSource(DataSourceConfiguration dataSource) {
		getSessionFactoryConfiguration().getDataSources().getDataSources().add(dataSource);
		return this;
	}
	
	public AbstractNoSQLPersistenceConfiguration packageScanEntity(String packageName) {
		getSessionFactoryConfiguration().getPackageToScanEntity().setPackageName(packageName);
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration addDataSource(String id, Class<?> clazz, PropertyConfiguration[] properties) {
		return addDataSource(id, clazz.getName(), properties);
	}

	public AbstractNoSQLPersistenceConfiguration addDataSource(String id, String clazz, PropertyConfiguration[] properties) {
		DataSourceConfiguration dataSource = new DataSourceConfiguration(id, clazz);
		for (PropertyConfiguration propertyConfiguration : properties) {
			dataSource.getProperties().add(propertyConfiguration);
		}
		getSessionFactoryConfiguration().getDataSources().getDataSources().add(dataSource);
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration addDataSource(String id, Class<?> clazz, Map<String, String> properties) {
		return addDataSource(id, clazz.getName(), properties);
	}

	public AbstractNoSQLPersistenceConfiguration addDataSource(String id, String clazz, Map<String, String> properties) {
		List<PropertyConfiguration> props = new ArrayList<PropertyConfiguration>();
		for (String property : properties.keySet()) {
			props.add(new PropertyConfiguration().setName(property).setValue(properties.get(property)));
		}
		return addDataSource(id, clazz, props.toArray(new PropertyConfiguration[] {}));
	}

	public AbstractNoSQLPersistenceConfiguration addDataSource(String id, Class<?> clazz, Properties properties) {
		return addDataSource(id, clazz.getName(), properties);
	}

	public AbstractNoSQLPersistenceConfiguration addDataSource(String id, String clazz, Properties properties) {
		List<PropertyConfiguration> props = new ArrayList<PropertyConfiguration>();
		for (Object property : properties.keySet()) {
			props.add(new PropertyConfiguration().setName((String) property).setValue((String) properties.get(property)));
		}
		return addDataSource(id, clazz, props.toArray(new PropertyConfiguration[] {}));
	}

	public AbstractNoSQLPersistenceConfiguration addProperty(PropertyConfiguration property) {
		getSessionFactoryConfiguration().getProperties().getProperties().add(property);
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration addProperties(Properties properties) {
		for (Object property : properties.keySet()) {
			addProperty(new PropertyConfiguration().setName((String) property).setValue((String) properties.get(property)));
		}
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration addProperties(PropertyConfiguration[] properties) {
		for (PropertyConfiguration property : properties) {
			addProperty(property);
		}
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration addProperty(String name, String value) {
		addProperty(new PropertyConfiguration().setName(name).setValue(value));
		return this;
	}

	protected void prepareClassesToLoad() throws ClassNotFoundException {
		LOG.debug("Preparando classes para ler entidades.");
		if ((getSessionFactoryConfiguration().getPackageToScanEntity() != null)
				&& (!"".equals(getSessionFactoryConfiguration().getPackageToScanEntity().getPackageName()))) {
			if (getSessionFactoryConfiguration().isIncludeSecurityModel())
				getSessionFactoryConfiguration().getPackageToScanEntity().setPackageName(
						getSessionFactoryConfiguration().getPackageToScanEntity().getPackageName() + ", " + SECURITY_PACKAGE);
			String[] packages = StringUtils.tokenizeToStringArray(getSessionFactoryConfiguration().getPackageToScanEntity().getPackageName(), ", ;");
			List<Class<?>> scanClasses = ClassPathScanner.scanClasses(new ClassFilter().packages(packages).annotation(Entity.class).annotation(Embedded.class).annotation(Converters.class)
					.annotation(EnumValues.class).packageName(CONVERTERS_PACKAGE));
			if (LOG.isDebugEnabled()) {
				for (Class<?> cl : scanClasses) {
					LOG.debug("Encontrado classe scaneada " + cl.getName());
				}
			}
			getSessionFactoryConfiguration().addToAnnotatedClasses(scanClasses);
		}

		if ((getSessionFactoryConfiguration().getClasses() == null) || (getSessionFactoryConfiguration().getClasses().size() == 0))
			LOG.debug("Não foram encontradas classes representando entidades. Informe o pacote onde elas podem ser localizadas ou informe manualmente cada uma delas.");

		LOG.debug("Preparação das classes concluída.");
	}

	public abstract NoSQLSessionFactory buildSessionFactory() throws Exception;

	public NoSQLDescriptionEntityManager loadEntities(NoSQLDialect dialect) throws Exception {
		List<Class<? extends Serializable>> classes = getSessionFactoryConfiguration().getClasses();
		Collections.sort(classes, new DependencyComparator());

		if (modelConfiguration != null)
			this.descriptionEntityManager.load(modelConfiguration, getPropertyAccessorFactory(), dialect);
		else
			this.descriptionEntityManager.load(classes, true, getPropertyAccessorFactory(), dialect);
		return this.descriptionEntityManager;
	}

	@Override
	public AbstractNoSQLPersistenceConfiguration configure() throws AnterosNoSQLConfigurationException {
		return configure(AnterosCoreProperties.XML_CONFIGURATION);
	}

	@Override
	public AbstractNoSQLPersistenceConfiguration configure(String xmlFile) throws AnterosNoSQLConfigurationException {
		InputStream is;
		try {
			final List<URL> resources = ResourceUtils.getResources(xmlFile, getClass());
			if ((resources != null) && (resources.size() > 0)) {
				final URL url = resources.get(0);
				is = url.openStream();
				configure(is);
				return this;
			}
		} catch (final Exception e) {
			throw new AnterosNoSQLConfigurationException("Impossível realizar a leitura " + xmlFile + " " + e);
		}

		throw new AnterosNoSQLConfigurationException("Arquivo de configuração " + xmlFile + " não encontrado.");
	}

	@Override
	public AbstractNoSQLPersistenceConfiguration configure(InputStream xmlConfiguration) throws AnterosNoSQLConfigurationException {
		try {
			final AbstractNoSQLPersistenceConfiguration result = parseXmlConfiguration(xmlConfiguration);
			this.setSessionFactory(result.getSessionFactoryConfiguration());
			this.dataSource = null;
			this.buildDataSource();
			return this;
		} catch (final InvocationTargetException e) {
			throw new AnterosNoSQLConfigurationException("Impossível realizar a leitura do arquivo de configuração." + e.getTargetException());
		} catch (final Exception e) {
			throw new AnterosNoSQLConfigurationException("Impossível realizar a leitura do arquivo de configuração." + e);
		}
	}

	protected abstract AbstractNoSQLPersistenceConfiguration parseXmlConfiguration(InputStream xmlConfiguration) throws Exception;

	protected abstract void buildDataSource() throws Exception;

	public NoSQLDataSource getDataSource() {
		return dataSource;
	}

	public AbstractNoSQLPersistenceConfiguration dataSource(NoSQLDataSource dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration modelConfiguration(NoSQLPersistenceModelConfiguration modelConfiguration) {
		this.modelConfiguration = modelConfiguration;
		return this;
	}

	public NoSQLDescriptionEntityManager getDescriptionEntityManager() {
		return descriptionEntityManager;
	}

	public String getProperty(String name) {
		return getSessionFactoryConfiguration().getProperties().getProperty(name);
	}

	public AbstractNoSQLPersistenceConfiguration setPlaceHolder(InputStream placeHolder) throws IOException {
		if (placeHolder != null) {
			Properties props = new Properties();
			props.load(placeHolder);
			getSessionFactoryConfiguration().getPlaceholder().setProperties(props);
		}
		return this;
	}

	public AbstractNoSQLPersistenceConfiguration setProperties(Properties props) {
		getSessionFactoryConfiguration().getProperties().setProperties(props);
		return this;
	}

	@Override
	public AbstractNoSQLPersistenceConfiguration configure(InputStream xmlConfiguration, InputStream placeHolder) throws AnterosNoSQLConfigurationException {
		try {
			final NoSQLBasicConfiguration result = parseXmlConfiguration(xmlConfiguration);
			result.setPlaceHolder(placeHolder);
			this.setSessionFactory(result.getSessionFactoryConfiguration());
			this.dataSource = null;
			this.buildDataSource();

			return this;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new AnterosNoSQLConfigurationException("Impossível realizar a leitura do arquivo de configuração." + e);
		}
	}

	public abstract PropertyAccessorFactory getPropertyAccessorFactory();

	public AbstractNoSQLPersistenceConfiguration withoutTransactionControl(boolean value) {
		getSessionFactoryConfiguration().setWithoutTransactionControl(value);
		return this;
	}	

}
