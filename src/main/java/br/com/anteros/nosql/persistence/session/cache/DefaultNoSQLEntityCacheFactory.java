package br.com.anteros.nosql.persistence.session.cache;

/**
 * Default implementation of cache factory, returning the default entity cache.
 */
public class DefaultNoSQLEntityCacheFactory implements NoSQLEntityCacheFactory {

    /**
     * Creates a new DefaultEntityCache
     *
     * @return the cache
     */
    public NoSQLEntityCache createCache() {
        return new DefaultNoSQLEntityCache();
    }
}
