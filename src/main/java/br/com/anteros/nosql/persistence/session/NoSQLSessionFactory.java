package br.com.anteros.nosql.persistence.session;

import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;

public interface NoSQLSessionFactory {
	
	
	/**
	 * Retorna a NoSQLSession da thread corrente
	 */
	public NoSQLSession<?> getCurrentSession();

	/**
	 * Retorna uma nova NoSQLSession a cada vez que é executado
	 */
	public NoSQLSession<?> openSession();
	
	/**
	 * Retorna uma nova NoSQLSession a cada vez que é executado
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public NoSQLSession<?> openSession(NoSQLConnection connection);
	
	/**
	 * Retorna o cache das descrições das entidades NoSQL
	 * @return
	 */
	public NoSQLDescriptionEntityManager getDescriptionEntityManager();

}
