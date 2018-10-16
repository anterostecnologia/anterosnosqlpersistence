package br.com.anteros.nosql.persistence.session;

import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.metadata.NoSQLEntityManaged;


public interface NoSQLPersistenceContext  {

	public NoSQLEntityManaged addEntityManaged(Object key, boolean readOnly, boolean newEntity, boolean checkIfExists) throws Exception;
	
	public NoSQLEntityManaged getEntityManaged(Object key);
	
	public boolean isExistsEntityManaged(Object key);
	
	public void removeEntityManaged(Object key);
	
	public void onBeforeExecuteCommit(NoSQLConnection connection) throws Exception;

	public void onBeforeExecuteRollback(NoSQLConnection connection) throws Exception;

	public void onAfterExecuteCommit(NoSQLConnection connection) throws Exception;

	public void onAfterExecuteRollback(NoSQLConnection connection) throws Exception;
	
	public Object getObjectFromCache(Object key);
	
	public void addObjectToCache(Object key, Object value);
	
	public void addObjectToCache(Object key, Object value, int secondsToLive);
	
	public NoSQLEntityManaged createEmptyEntityManaged(Object key);
	
	/**
	 * Remove todas as instâncias dos objetos da classe passada por parâmetro
	 * gerenciadas pela sessão
	 * 
	 * @param object
	 */
	public void evict(Class class0);

	/**
	 * Limpa o cache de entidades gerenciadas da sessão
	 */
	public void evictAll();
	
	public void detach(Object entity);

	public void clearCache();
	
	public boolean isWithoutTransactionControl();
	
}
