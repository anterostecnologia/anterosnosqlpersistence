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
package br.com.anteros.nosql.persistence.session.configuration.impl;

import java.io.InputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.com.anteros.core.utils.IOUtils;
import br.com.anteros.core.utils.ObjectUtils;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.nosql.persistence.client.NoSQLDataSource;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.accessor.PropertyAccessorFactory;
import br.com.anteros.nosql.persistence.metadata.configuration.AnterosNoSQLProperties;
import br.com.anteros.nosql.persistence.metadata.configuration.NoSQLPersistenceModelConfiguration;
import br.com.anteros.nosql.persistence.session.NoSQLSessionException;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.configuration.AbstractNoSQLPersistenceConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.DataSourceConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.DataSourcesConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.NoSQLSessionFactoryConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.PackageScanEntity;
import br.com.anteros.nosql.persistence.session.configuration.PlaceholderConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.PropertyConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.exception.AnterosNoSQLConfigurationException;
import br.com.anteros.nosql.persistence.session.impl.SimpleNoSQLSessionFactory;
import br.com.anteros.xml.helper.XMLReader;


public class AnterosNoSQLPersistenceConfiguration extends AnterosNoSQLPersistenceConfigurationBase {

	public AnterosNoSQLPersistenceConfiguration() {
		super();
	}
	
	public static AnterosNoSQLPersistenceConfiguration newConfiguration() {
		return new AnterosNoSQLPersistenceConfiguration();
	}

	public AnterosNoSQLPersistenceConfiguration(NoSQLDataSource dataSource) {
		super(dataSource);
	}

	public AnterosNoSQLPersistenceConfiguration(NoSQLPersistenceModelConfiguration modelConfiguration) {
		super(modelConfiguration);
	}

	public AnterosNoSQLPersistenceConfiguration(NoSQLDataSource dataSource, NoSQLPersistenceModelConfiguration modelConfiguration) {
		super(dataSource, modelConfiguration);
	}

	@Override
	public PropertyAccessorFactory getPropertyAccessorFactory() {
		// return new PropertyAcessorFactoryImpl();
		return null;
	}

	public NoSQLSessionFactory buildSessionFactory() throws Exception {
		
		if (getSessionFactoryConfiguration().getProperty(AnterosNoSQLProperties.DIALECT) == null) {
			throw new NoSQLSessionException("Dialeto não definido. Não foi possível instanciar NoSQLSessionFactory.");
		}

		String dialectProperty = getSessionFactoryConfiguration().getProperty(AnterosNoSQLProperties.DIALECT);
		Class<?> dialectClass = Class.forName(dialectProperty);

		if (!ReflectionUtils.isExtendsClass(NoSQLDialect.class, dialectClass))
			throw new NoSQLSessionException("A classe " + dialectClass.getName() + " não implementa a classe "
					+ NoSQLDialect.class.getName() + ".");

		this.dialect = (NoSQLDialect) dialectClass.newInstance();
		
		
		prepareClassesToLoad();		
		buildDataSource();		
		SimpleNoSQLSessionFactory sessionFactory = new SimpleNoSQLSessionFactory(descriptionEntityManager, dataSource, this.getSessionFactoryConfiguration());
		if (dataSource == null)
			throw new AnterosNoSQLConfigurationException("Datasource não configurado");
		loadEntities(sessionFactory.getDialect());
//		sessionFactory.generateDDL();
		return sessionFactory;
	}

	@Override
	protected AbstractNoSQLPersistenceConfiguration parseXmlConfiguration(InputStream xmlConfiguration) throws Exception {
		String xml = IOUtils.toString(xmlConfiguration);

		/*
		 * Cria a configuração da fábrica de sessões baseado no xml de configuração.
		 */
		NoSQLSessionFactoryConfiguration sessionFactoryConfiguration = new NoSQLSessionFactoryConfiguration();
		sessionFactoryConfiguration.setPlaceholder(new PlaceholderConfiguration(XMLReader.readAttributeFromXML(xml, SESSION_FACTORY_PATH + "/" + PLACEHOLDER,
				LOCATION)));
		sessionFactoryConfiguration.setPackageToScanEntity(new PackageScanEntity(XMLReader.readAttributeFromXML(xml, SESSION_FACTORY_PATH + "/"
				+ PACKAGE_SCAN_ENTITY, PACKAGE_NAME)));
		sessionFactoryConfiguration.setIncludeSecurityModel((Boolean) ObjectUtils.convert(
				XMLReader.readElementFromXML(xml, SESSION_FACTORY_PATH + "/" + INCLUDE_SECURITY_MODEL), Boolean.class));

		DataSourcesConfiguration dataSourcesConfiguration = new DataSourcesConfiguration();
		sessionFactoryConfiguration.setDataSources(dataSourcesConfiguration);
		NodeList dataSources = XMLReader.readNodesFromXML(xml, SESSION_FACTORY_PATH + "/" + DATA_SOURCES);

		/*
		 * Lê as fontes de dados
		 */
		for (int i = 0, length = dataSources.getLength(); i < length; i++) {
			Node dataSource = dataSources.item(i);
			DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration(XMLReader.readAttributeFromNode(dataSource, DATA_SOURCE, ID),
					XMLReader.readAttributeFromNode(dataSource, DATA_SOURCE, CLASS_NAME));
			NodeList properties = XMLReader.readNodesFromNode(dataSource, DATA_SOURCE_PROPERTY);
			for (int z = 0, zLength = properties.getLength(); z < zLength; z++) {
				Node property = properties.item(z);
				PropertyConfiguration propertyConfiguration = new PropertyConfiguration(XMLReader.readAttributeFromNode(property, PROPERTY, NAME),
						XMLReader.readAttributeFromNode(property, PROPERTY, VALUE));
				dataSourceConfiguration.getProperties().add(propertyConfiguration);
			}
			dataSourcesConfiguration.getDataSources().add(dataSourceConfiguration);
		}

		/*
		 * Lê as propriedades
		 */
		NodeList sessionProperties = XMLReader.readNodesFromXML(xml, SESSION_FACTORY_PATH + "/" + PROPERTIES);
		for (int z = 0, zLength = sessionProperties.getLength(); z < zLength; z++) {
			Node property = sessionProperties.item(z);
			sessionFactoryConfiguration.addProperty(XMLReader.readAttributeFromNode(property, PROPERTY, NAME),
					XMLReader.readAttributeFromNode(property, PROPERTY, VALUE));
		}

		/*
		 * Lê a lista de classes anotadas
		 */
		NodeList annotatedClasses = XMLReader.readNodesFromXML(xml, SESSION_FACTORY_PATH + "/" + ANNOTATED_CLASSES);
		for (int z = 0, zLength = annotatedClasses.getLength(); z < zLength; z++) {
			Node property = annotatedClasses.item(z);
			sessionFactoryConfiguration.addAnnotatedClass(XMLReader.readElementFromNode(property, CLASS_NAME));
		}
		
		setSessionFactory(sessionFactoryConfiguration);
		return this;
	}

	public AnterosNoSQLPersistenceConfiguration setPackageToScanEntity(PackageScanEntity packageToScanEntity){
		getSessionFactoryConfiguration().setPackageToScanEntity(packageToScanEntity);
		return this;
	}
}
