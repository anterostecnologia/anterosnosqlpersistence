package br.com.anteros.nosql.persistence.client;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.ShowCommandsType;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionFactory;

public interface NoSQLSessionBuilder {
	
	public NoSQLSessionBuilder sessionFactory(NoSQLSessionFactory sessionFactory);
	
	public NoSQLSessionBuilder connection(NoSQLConnection connection);
	
	public NoSQLSessionBuilder descriptionEntityManager(NoSQLDescriptionEntityManager descriptionEntityManager);
	
	public NoSQLSessionBuilder showCommands(ShowCommandsType[] showCommands);
	
	public NoSQLSessionBuilder formatCommands(boolean formatCommands);
	
	public NoSQLSessionBuilder useBeanValidation(boolean useBeanValidation);
	
	public NoSQLSessionBuilder transactionFactory(NoSQLTransactionFactory transactionFactory);
	
	public NoSQLSessionBuilder withoutTransactionControl(boolean value);

	public NoSQLSession<?> build();

}
