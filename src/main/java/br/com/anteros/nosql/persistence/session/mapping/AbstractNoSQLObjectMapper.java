package br.com.anteros.nosql.persistence.session.mapping;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.converters.NoSQLConverters;
import br.com.anteros.nosql.persistence.converters.NoSQLCustomConverters;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntity;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.proxy.LazyFeatureDependencies;
import br.com.anteros.nosql.persistence.proxy.LazyProxyFactory;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.cache.NoSQLEntityCache;

public abstract class AbstractNoSQLObjectMapper {
	
	private static Logger LOG = LoggerProvider.getInstance().getLogger(AbstractNoSQLObjectMapper.class.getName());

	public static final String ID_KEY = "_id";

	public static final String IGNORED_FIELDNAME = ".";

	public static final String CLASS_NAME_FIELDNAME = "className";

	protected NoSQLMapperOptions options;

	protected NoSQLDescriptionEntityManager descriptionEntityManager;

	protected List<NoSQLEntityInterceptor> interceptors = new LinkedList<NoSQLEntityInterceptor>();

	protected NoSQLConverters converters;

	protected LazyProxyFactory proxyFactory = LazyFeatureDependencies.createDefaultProxyFactory();
	
	protected Map<Class<?>, Object> instanceCache = new ConcurrentHashMap<>();

	protected AbstractNoSQLObjectMapper(NoSQLDescriptionEntityManager descriptionEntityManager,
			NoSQLMapperOptions options) {
		this.options = options;
		this.descriptionEntityManager = descriptionEntityManager;
		this.converters = new NoSQLCustomConverters(this, descriptionEntityManager.getDialect());
	}

	public NoSQLMapperOptions getOptions() {
		return options;
	}

	public void setOptions(NoSQLMapperOptions options) {
		this.options = options;
	}

	public void addInterceptor(final NoSQLEntityInterceptor ei) {
		interceptors.add(ei);
	}

	public NoSQLConverters getConverters() {
		return converters;
	}

	public void setConverters(NoSQLConverters converters) {
		this.converters = converters;
	}

	public NoSQLDescriptionEntityManager getDescriptionEntityManager() {
		return descriptionEntityManager;
	}

	public Class<?> getClassFromCollection(String collectionName) {
		List<NoSQLDescriptionEntity> entitiesByCollectionName = descriptionEntityManager
				.getDescriptionEntitiesByCollectionName(collectionName);
		if (entitiesByCollectionName != null && entitiesByCollectionName.size() > 0) {
			return entitiesByCollectionName.get(0).getEntityClass();
		}
		return null;
	}

	public abstract Object keyToDBRef(Key<?> key);

	public abstract Object keyToId(Key<?> value);

	public LazyProxyFactory getProxyFactory() {
		return proxyFactory;
	}

    public Map<Class<?>, Object> getInstanceCache() {
        return instanceCache;
    }
    
    public NoSQLEntityCache createEntityCache() {
        return getOptions().getCacheFactory().createCache();
    }

	public abstract <T> Key<T> manualRefToKey(final Class<T> type, final Object id);

	public abstract <T> Key<T> refToKey(final Object ref);

	public abstract <T> List<Key<T>> getKeysByManualRefs(final Class<T> clazz, final List<Object> refs);

	public abstract <T> List<Key<T>> getKeysByRefs(final List<Object> refs);
	
	public abstract <T> Key<T> createKey(final Class<T> clazz, final Serializable id);

	public abstract <T> Key<T> createKey(final Class<T> clazz, final Object id);

	public abstract boolean isMapped(Class<?> clazz);
	
	public Object toDocument(final Object entity) {
        return toDocument(entity, null);
    }

    public Object toDocument(final Object entity, final Map<Object, Object> involvedObjects) {
        return toDocument(entity, involvedObjects, true);
    }
    
    public abstract Object toDocument(final Object entity, final Map<Object, Object> involvedObjects, final boolean lifecycle);
    
    public abstract <T> T fromDocument(final NoSQLSession<?> session, final Object dbObject, final T entity, final NoSQLEntityCache cache) throws Exception;
    
    public abstract <T> T fromDocument(final NoSQLSession<?> session, final Class<T> entityClass, final Object dbDoc,
			final NoSQLEntityCache cache) throws Exception;

	public List<NoSQLEntityInterceptor> getInterceptors() {
		return interceptors;
	}
	

    
}