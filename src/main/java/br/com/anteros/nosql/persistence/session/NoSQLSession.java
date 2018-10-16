package br.com.anteros.nosql.persistence.session;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;
import br.com.anteros.nosql.persistence.session.query.NoSQLQuery;
import br.com.anteros.nosql.persistence.session.query.NoSQLUpdate;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransaction;

public interface NoSQLSession<Q> {

	public <T> boolean collectionExists(Class<T> entityClass);

	public boolean collectionExists(String collectionName);

	public <T> long count(NoSQLQuery<Q> query, Class<?> entityClass);

	public <T> long count(NoSQLQuery<Q> query, Class<?> entityClass, String collectionName);

	public <T> long count(NoSQLQuery<Q> query, String collectionName);

	public <T> void dropCollection(Class<T> entityClass);

	public void dropCollection(String collectionName);
	
	public <T> boolean exists(NoSQLQuery<Q> query, Class<?> entityClass);

	public <T> boolean exists(NoSQLQuery<Q> query, Class<?> entityClass, String collectionName);

	public <T> boolean exists(NoSQLQuery<Q> query, String collectionName);

	public <T> List<T> find(NoSQLQuery<Q> query, Class<T> entityClass);

	public <T> List<T> find(NoSQLQuery<Q> query, Class<T> entityClass, String collectionName);

	public <T> List<T> findAll(Class<T> entityClass);

	public <T> List<T> findAll(Class<T> entityClass, String collectionName);

	public <T> List<T> findAllAndRemove(NoSQLQuery<Q> query, Class<T> entityClass);

	public <T> List<T> findAllAndRemove(NoSQLQuery<Q> query, Class<T> entityClass, String collectionName);

	public <T> List<T> findAllAndRemove(NoSQLQuery<Q> query, String collectionName);

	public <T> T findAndModify(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<T> entityClass);

	public <T> T findAndModify(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<T> entityClass, String collectionName);
	
	public <T> T findAndModify(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, FindAndModifyOptions options, Class<T> entityClass);
	
	public <T> T findAndModify(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, FindAndModifyOptions options, Class<T> entityClass,
			String collectionName);
	
	public <S, T> T findAndReplace(NoSQLQuery<Q> query, S replacement, FindAndReplaceOptions options, Class<S> entityType,
			String collectionName, Class<T> resultType);

	public <T> T findAndRemove(NoSQLQuery<Q> query, Class<T> entityClass);

	public <T> T findAndRemove(NoSQLQuery<Q> query, Class<T> entityClass, String collectionName);

	public <T> T findById(Object id, Class<T> entityClass);

	public <T> T findById(Object id, Class<T> entityClass, String collectionName);
	
	public <T> T findOne(NoSQLQuery<Q> query, Class<T> entityClass) ;

	public <T> T findOne(NoSQLQuery<Q> query, Class<T> entityClass, String collectionName);

	public String getCollectionName(Class<?> entityClass);
	
	public String getCollectionName(Object entity);

	public Set<String> getCollectionNames();

	public <T> Collection<T> insert(Collection<? extends Object> batchToSave, Class<?> entityClass) throws Exception;

	public <T> Collection<T> insert(Collection<? extends Object> batchToSave, String collectionName) throws Exception;

	public <T> T insert(T objectToSave) throws Exception;

	public <T> T insert(T objectToSave, String collectionName) throws Exception;

	public <T> Collection<T> insertAll(Collection<? extends Object> objectsToSave) throws Exception;

	public NoSQLResult remove(Object object, String collectionName) throws Exception;
	
	public NoSQLResult remove(Object object);

	public <T> NoSQLResult remove(NoSQLQuery<Q> query, Class<?> entityClass);

	public <T> NoSQLResult remove(NoSQLQuery<Q> query, Class<?> entityClass, String collectionName);

	public <T> NoSQLResult remove(NoSQLQuery<Q> query, String collectionName);

	public <T> T save(T objectToSave);

	public <T> T save(T objectToSave, String collectionName);

	public <T> NoSQLResult updateFirst(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<?> entityClass) throws Exception;

	public <T> NoSQLResult updateFirst(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<?> entityClass, String collectionName)
			throws Exception;

	public <T> NoSQLResult updateFirst(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, String collectionName) throws Exception;

	public <T> NoSQLResult updateMulti(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<?> entityClass) throws Exception;

	public <T> NoSQLResult updateMulti(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<?> entityClass, String collectionName)
			throws Exception;

	public <T> NoSQLResult updateMulti(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, String collectionName) throws Exception;

	public <T> NoSQLResult upsert(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<?> entityClass) throws Exception;

	public <T> NoSQLResult upsert(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, Class<?> entityClass, String collectionName)
			throws Exception;

	public <T> NoSQLResult upsert(NoSQLQuery<Q> query, NoSQLUpdate<Q> update, String collectionName) throws Exception;

	public void flush() throws Exception;

	public void forceFlush(Set<String> collectionNames) throws Exception;

	public void close() throws Exception;

	public void onBeforeExecuteCommit(NoSQLConnection connection) throws Exception;

	public void onBeforeExecuteRollback(NoSQLConnection connection) throws Exception;

	public void onAfterExecuteCommit(NoSQLConnection connection) throws Exception;

	public void onAfterExecuteRollback(NoSQLConnection connection) throws Exception;

	public NoSQLDescriptionEntityManager getDescriptionEntityManager();

	public NoSQLDialect getDialect();

	public NoSQLConnection getConnection();

	public NoSQLPersistenceContext getPersistenceContext();

	public void addListener(NoSQLSessionListener listener);

	public void removeListener(NoSQLSessionListener listener);

	public List<NoSQLSessionListener> getListeners();

//	public List<CommandNoSQL> getCommandQueue();

	public void setFormatCommands(boolean format);

//	public void setShowCommands(ShowCommandsType... sql);

	public boolean isShowCommands();

//	public ShowCommandsType[] getFormatCommands();

	public boolean isFormatCommands();

//	public EntityHandler createNewEntityHandler(Class<?> resultClass, List<ExpressionFieldMapper> expressionsFieldMapper,
//			Map<SQLQueryAnalyserAlias, Map<String, String[]>> columnAliases, Cache transactionCache, boolean allowDuplicateObjects,
//			Object objectToRefresh, int firstResult, int maxResults, boolean readOnly, LockOptions lockOptions) throws Exception;

	public boolean isProxyObject(Object object) throws Exception;

	public boolean proxyIsInitialized(Object object) throws Exception;

	public <T> T cloneEntityManaged(Object object) throws Exception;

	public boolean isClosed() throws Exception;

	public NoSQLTransaction getTransaction();

	public NoSQLSessionFactory getNoSQLSessionFactory();

	public void clear() throws Exception;

	public boolean validationIsActive();

	public void activateValidation();

	public void deactivateValidation();

	public <T> Object getIdentifier(T owner) throws Exception;
	
	public <T> Key<T> getKey(T entity);
	
	public AbstractNoSQLObjectMapper mapper();
	
	public void commitTransaction();
	
	public void startTransaction();
	
	public void abortTransaction();

	public <T> long count(Class<T> entityClass);
	
	public boolean isWithoutTransactionControl();

}
