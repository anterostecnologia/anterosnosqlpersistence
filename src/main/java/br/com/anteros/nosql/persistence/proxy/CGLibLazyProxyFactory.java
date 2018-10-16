package br.com.anteros.nosql.persistence.proxy;


import java.io.Serializable;
import java.util.Collection;
import java.util.Map;


import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;
import com.thoughtworks.proxy.toys.dispatch.Dispatching;

import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.session.NoSQLSession;


public class CGLibLazyProxyFactory implements LazyProxyFactory {
    private final CglibProxyFactory factory = new CglibProxyFactory();

    @Override
    public <T extends Collection> T createListProxy(final NoSQLSession session, final T listToProxy, final Class referenceObjClass,
                                                    final boolean ignoreMissing) {
        final Class<? extends Collection> targetClass = listToProxy.getClass();
        final CollectionObjectReference objectReference = new CollectionObjectReference(listToProxy, referenceObjClass, ignoreMissing,
                                                                                        session);

        final T backend = (T) new NonFinalizingHotSwappingInvoker(new Class[]{targetClass, Serializable.class}, factory, objectReference,
                                                                  DelegationMode.SIGNATURE).proxy();

        return (T) Dispatching.proxy(targetClass, new Class[]{ProxiedEntityReferenceList.class, targetClass, Serializable.class})
                              .with(objectReference, backend)
                              .build(factory);

    }

    @Override
    public <T extends Map> T createMapProxy(final NoSQLSession session, final T mapToProxy, final Class referenceObjClass,
                                            final boolean ignoreMissing) {
        final Class<? extends Map> targetClass = mapToProxy.getClass();
        final MapObjectReference objectReference = new MapObjectReference(session, mapToProxy, referenceObjClass, ignoreMissing);

        final T backend = (T) new NonFinalizingHotSwappingInvoker(new Class[]{targetClass, Serializable.class}, factory, objectReference,
                                                                  DelegationMode.SIGNATURE).proxy();

        return (T) Dispatching.proxy(targetClass, new Class[]{ProxiedEntityReferenceMap.class, targetClass, Serializable.class})
                              .with(objectReference, backend)
                              .build(factory);

    }

    @Override
    public <T> T createProxy(final NoSQLSession session, final Class<T> targetClass, final Key<T> key, final boolean ignoreMissing) {

        final EntityObjectReference objectReference = new EntityObjectReference(session, targetClass, key, ignoreMissing);

        final T backend = (T) new NonFinalizingHotSwappingInvoker(new Class[]{targetClass, Serializable.class}, factory, objectReference,
                                                                  DelegationMode.SIGNATURE).proxy();

        return (T) Dispatching.proxy(targetClass, new Class[]{ProxiedEntityReference.class, targetClass, Serializable.class})
                              .with(objectReference, backend)
                              .build(factory);

    }
}
