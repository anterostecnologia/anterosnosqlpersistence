/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.com.anteros.nosql.persistence.session.configuration.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import br.com.anteros.core.utils.ResourceUtils;
import br.com.anteros.nosql.persistence.client.NoSQLDataSource;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.configuration.NoSQLPersistenceModelConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.AbstractNoSQLPersistenceConfiguration;
import br.com.anteros.nosql.persistence.session.configuration.exception.AnterosNoSQLConfigurationException;


public abstract class AnterosNoSQLPersistenceConfigurationBase extends AbstractNoSQLPersistenceConfiguration {
	
	protected NoSQLDialect dialect;

	

	public AnterosNoSQLPersistenceConfigurationBase() {
		super();
	}

	public AnterosNoSQLPersistenceConfigurationBase(NoSQLDataSource dataSource) {
		super(dataSource);
	}

	public AnterosNoSQLPersistenceConfigurationBase(NoSQLPersistenceModelConfiguration modelConfiguration) {
		super(modelConfiguration);
	}

	public AnterosNoSQLPersistenceConfigurationBase(NoSQLDataSource dataSource, NoSQLPersistenceModelConfiguration modelConfiguration) {
		super(dataSource, modelConfiguration);
	}

	@Override
	protected void buildDataSource() throws Exception {
		if (dataSource == null) {
			dataSource = dialect.getDataSourceBuilder().configure(getSessionFactoryConfiguration().getProperties()).build();
		}
		if (dataSource == null)
			throw new AnterosNoSQLConfigurationException("Datasource não configurado");
	}

	public static InputStream getDefaultXmlInputStream() throws Exception {
		List<URL> resources = ResourceUtils.getResources("/anteros-nosql-config.xml", AnterosNoSQLPersistenceConfigurationBase.class);
		if ((resources == null) || (resources.isEmpty())) {
			resources = ResourceUtils.getResources("/assets/anteros-nosql-config.xml", AnterosNoSQLPersistenceConfigurationBase.class);
			if ((resources != null) && (!resources.isEmpty())) {
				final URL url = resources.get(0);
				return url.openStream();
			}
		}
		return null;
	}

}
