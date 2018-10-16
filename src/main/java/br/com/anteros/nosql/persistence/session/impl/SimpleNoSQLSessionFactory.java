package br.com.anteros.nosql.persistence.session.impl;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.client.NoSQLDataSource;
import br.com.anteros.nosql.persistence.client.NoSQLSessionBuilder;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.session.AbstractNoSQLSessionFactoryBase;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.NoSQLSessionException;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.configuration.NoSQLSessionFactoryConfiguration;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionException;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionFactory;


public class SimpleNoSQLSessionFactory extends AbstractNoSQLSessionFactoryBase {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(NoSQLSessionFactory.class);

	private NoSQLTransactionFactory transactionFactory;
	private NoSQLDescriptionEntityManager descriptionEntityManager;
	private NoSQLDataSource dataSource;
	private NoSQLSessionFactoryConfiguration configuration;

	public SimpleNoSQLSessionFactory(NoSQLDescriptionEntityManager descriptionEntityManager, NoSQLDataSource dataSource,
			NoSQLSessionFactoryConfiguration configuration) throws Exception {
		super(descriptionEntityManager, dataSource, configuration);
		this.descriptionEntityManager = descriptionEntityManager;
		this.dataSource = dataSource;
		this.configuration = configuration;
	}

	@Override
	public NoSQLSession<?> getCurrentSession() {
		if (currentSessionContext == null) {
			throw new NoSQLSessionException("No CurrentSessionContext configured!");
		}
		return currentSessionContext.currentSession();
	}

	public NoSQLSession<?> openSession(){
		return openSession(this.getDatasource().getConnection());
	}

	@Override
	protected NoSQLTransactionFactory getTransactionFactory() {
		if (transactionFactory == null) {
			try {
				transactionFactory = buildTransactionFactory();
			} catch (Exception e) {
				throw new NoSQLTransactionException("Não foi possível criar a fábrica de transações.", e);
			}
		}
		return transactionFactory;
	}

	protected NoSQLTransactionFactory buildTransactionFactory() throws Exception {
		if (transactionFactory == null) {
			transactionFactory = this.getDialect().getTransactionFactory();
			LOG.info("instantiated TransactionFactory");
		}
		return transactionFactory;
	}

	@Override
	public NoSQLSession<?> openSession(NoSQLConnection connection) {
		NoSQLSessionBuilder sessionBuilder;
		try {
			sessionBuilder = this.dialect.getSessionBuilder();
			return sessionBuilder.connection(connection)
					.descriptionEntityManager(descriptionEntityManager).formatCommands(formatCommands).withoutTransactionControl(this.isWithoutTransactionControl())
					.showCommands(showCommands).useBeanValidation(useBeanValidation).sessionFactory(this).build();
		} catch (Exception e) {
			throw new NoSQLSessionFactoryException(e);
		}
		
	}

	@Override
	public NoSQLDescriptionEntityManager getDescriptionEntityManager() {
		return descriptionEntityManager;
	}

}