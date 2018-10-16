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
package br.com.anteros.nosql.persistence.session;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.client.NoSQLDataSource;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.metadata.configuration.AnterosNoSQLProperties;
import br.com.anteros.nosql.persistence.session.configuration.NoSQLSessionFactoryConfiguration;
import br.com.anteros.nosql.persistence.session.context.CurrentNoSQLSessionContext;
import br.com.anteros.nosql.persistence.session.context.ManagedNoSQLSessionContext;
import br.com.anteros.nosql.persistence.session.context.ThreadLocalNoSQLSessionContext;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionFactory;


public abstract class AbstractNoSQLSessionFactoryBase implements NoSQLSessionFactory {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(AbstractNoSQLSessionFactoryBase.class.getName());

	protected NoSQLDialect dialect;
	protected NoSQLDescriptionEntityManager descriptionEntityManager;
	protected NoSQLDataSource dataSource;
	protected NoSQLSessionFactoryConfiguration configuration;
	protected CurrentNoSQLSessionContext currentSessionContext;
	protected boolean useBeanValidation=true;
	protected ShowCommandsType[] showCommands = {ShowCommandsType.NONE};
	protected boolean formatCommands = false;
	protected boolean withoutTransactionControl = false;

	public AbstractNoSQLSessionFactoryBase(NoSQLDescriptionEntityManager descriptionEntityManager, NoSQLDataSource dataSource,
			NoSQLSessionFactoryConfiguration configuration) throws Exception {
		this.descriptionEntityManager = descriptionEntityManager;
		this.dataSource = dataSource;
		this.configuration = configuration;

		if (configuration.getProperty(AnterosNoSQLProperties.DIALECT) == null) {
			throw new NoSQLSessionException("Dialeto não definido. Não foi possível instanciar NoSQLSessionFactory.");
		}

		String dialectProperty = configuration.getProperty(AnterosNoSQLProperties.DIALECT);
		Class<?> dialectClass = Class.forName(dialectProperty);

		if (!ReflectionUtils.isExtendsClass(NoSQLDialect.class, dialectClass))
			throw new NoSQLSessionException("A classe " + dialectClass.getName() + " não implementa a classe "
					+ NoSQLDialect.class.getName() + ".");

		this.withoutTransactionControl = configuration.isWithoutTransactionControl();
		this.dialect = (NoSQLDialect) dialectClass.newInstance();
//		this.dialect.setDatabaseName(configuration.getProperty(AnterosNoSQLProperties.DATABASE_NAME));

		if (configuration.getProperty(AnterosNoSQLProperties.SHOW_COMMANDS) != null){
			String propertyShowCommands = configuration.getProperty(AnterosNoSQLProperties.SHOW_COMMANDS);
			String[] splitShowCommands = propertyShowCommands.split("\\,");
			this.showCommands = ShowCommandsType.parse(splitShowCommands);
		}

		if (configuration.getProperty(AnterosNoSQLProperties.FORMAT_COMMANDS) != null)
			this.formatCommands = new Boolean(configuration.getProperty(AnterosNoSQLProperties.FORMAT_COMMANDS));
		
		if (configuration.getProperty(AnterosNoSQLProperties.USE_BEAN_VALIDATION) != null)
			this.useBeanValidation = new Boolean(configuration.getProperty(AnterosNoSQLProperties.USE_BEAN_VALIDATION)).booleanValue();

		this.currentSessionContext = buildCurrentSessionContext();
	}

	@Override
	public NoSQLSession<?> getCurrentSession() {
		if (currentSessionContext == null) {
			throw new NoSQLSessionException("No CurrentSessionContext configured!");
		}
		return currentSessionContext.currentSession();
	}

	protected CurrentNoSQLSessionContext buildCurrentSessionContext() throws Exception {
		String impl = configuration.getProperty(AnterosNoSQLProperties.CURRENT_SESSION_CONTEXT);
		if ("thread".equals(impl)) {
			return new ThreadLocalNoSQLSessionContext(this);
		} else if ("managed".equals(impl)) {
			return new ManagedNoSQLSessionContext(this);
		} else {
			return new ThreadLocalNoSQLSessionContext(this);
		}
	}

	protected abstract NoSQLTransactionFactory getTransactionFactory();

	

	public NoSQLDialect getDialect() {
		return dialect;
	}

	public void setDialect(NoSQLDialect dialect) {
		this.dialect = dialect;
	}

	public NoSQLDescriptionEntityManager getDescriptionEntityManager() {
		return descriptionEntityManager;
	}

	public void setDescriptionEntityManager(NoSQLDescriptionEntityManager descriptionEntityManager) {
		this.descriptionEntityManager = descriptionEntityManager;
	}

	public NoSQLDataSource getDatasource() {
		return dataSource;
	}

	public void setDatasource(NoSQLDataSource datasource) {
		this.dataSource = datasource;
	}

	public boolean isShowCommands() {
		return showCommands!=null;
	}

	public void setShowCommands(ShowCommandsType... showCommands) {
		this.showCommands = showCommands;
	}

	public boolean isFormatCommands() {
		return formatCommands;
	}

	public void setFormatCommands(boolean formatCommands) {
		this.formatCommands = formatCommands;
	}

	public void onBeforeExecuteCommit(NoSQLConnection connection) throws Exception {
		NoSQLSession<?> session = getCurrentSession();
		if (session != null)
			session.getPersistenceContext().onBeforeExecuteCommit(connection);
	}

	public void onBeforeExecuteRollback(NoSQLConnection connection) throws Exception {
		NoSQLSession<?> session = getCurrentSession();
		if (session != null)
			session.getPersistenceContext().onBeforeExecuteRollback(connection);
	}

	public void onAfterExecuteCommit(NoSQLConnection connection) throws Exception {
		NoSQLSession<?> session = getCurrentSession();
		if (session != null)
			session.getPersistenceContext().onAfterExecuteCommit(connection);
	}

	public void onAfterExecuteRollback(NoSQLConnection connection) throws Exception {
		NoSQLSession<?> session = getCurrentSession();
		if (session != null)
			session.getPersistenceContext().onAfterExecuteRollback(connection);
	}

	public NoSQLDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(NoSQLDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public NoSQLSessionFactoryConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(NoSQLSessionFactoryConfiguration configuration) {
		this.configuration = configuration;
	}

	public ShowCommandsType[] getShowCommands() {
		return showCommands;
	}
	
	public boolean isUseBeanValidation() {
		return useBeanValidation;
	}

	public void setUseBeanValidation(boolean useBeanValidation) {
		this.useBeanValidation = useBeanValidation;
	}

	public boolean isWithoutTransactionControl() {
		return withoutTransactionControl;
	}
	
}
