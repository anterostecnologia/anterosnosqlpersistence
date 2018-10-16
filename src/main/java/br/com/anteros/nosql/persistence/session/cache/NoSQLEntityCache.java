package br.com.anteros.nosql.persistence.session.cache;


import br.com.anteros.nosql.persistence.converters.Key;

public interface NoSQLEntityCache {

    Boolean exists(Key<?> k);

    void flush();

    <T> T getEntity(Key<T> k);

    <T> T getProxy(Key<T> k);

    void notifyExists(Key<?> k, boolean exists);

    <T> void putEntity(Key<T> k, T t);

    <T> void putProxy(Key<T> k, T t);

    NoSQLEntityCacheStatistics stats();
}
