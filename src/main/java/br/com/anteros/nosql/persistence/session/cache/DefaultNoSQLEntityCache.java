package br.com.anteros.nosql.persistence.session.cache;


import java.util.HashMap;
import java.util.Map;

import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.proxy.LazyFeatureDependencies;
import br.com.anteros.nosql.persistence.proxy.ProxyHelper;



public class DefaultNoSQLEntityCache implements NoSQLEntityCache {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private final Map entityMap = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.WEAK);
    private final Map proxyMap = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
    private final Map<Key, Boolean> existenceMap = new HashMap<Key, Boolean>();
    private final NoSQLEntityCacheStatistics stats = new NoSQLEntityCacheStatistics();

    @Override
    public Boolean exists(final Key<?> k) {
        if (entityMap.containsKey(k)) {
            stats.incHits();
            return true;
        }

        final Boolean b = existenceMap.get(k);
        if (b == null) {
            stats.incMisses();
        } else {
            stats.incHits();
        }
        return b;
    }

    @Override
    public void flush() {
        entityMap.clear();
        existenceMap.clear();
        proxyMap.clear();
        stats.reset();
    }

    @Override
    public <T> T getEntity(final Key<T> k) {
        final Object o = entityMap.get(k);
        if (o == null) {
            if (LazyFeatureDependencies.testDependencyFullFilled()) {
                final Object proxy = proxyMap.get(k);
                if (proxy != null) {
                    stats.incHits();
                    return (T) ProxyHelper.unwrap(proxy);
                }
            }
            // System.out.println("miss entity " + k + ":" + this);
            stats.incMisses();
        } else {
            stats.incHits();
        }
        return (T) o;
    }

    @Override
    public <T> T getProxy(final Key<T> k) {
        final Object o = proxyMap.get(k);
        if (o == null) {
            stats.incMisses();
        } else {
            stats.incHits();
        }
        return (T) o;
    }

    @Override
    public void notifyExists(final Key<?> k, final boolean exists) {
        final Boolean put = existenceMap.put(k, exists);
        if (put == null || !put) {
            stats.incEntities();
        }
    }

    @Override
    public <T> void putEntity(final Key<T> k, final T t) {
        notifyExists(k, true); // already registers a write
        entityMap.put(k, t);
    }

    @Override
    public <T> void putProxy(final Key<T> k, final T t) {
        proxyMap.put(k, t);
        stats.incEntities();

    }

    @Override
    public NoSQLEntityCacheStatistics stats() {
        return stats.copy();
    }

}
