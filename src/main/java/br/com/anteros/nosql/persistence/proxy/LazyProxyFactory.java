package br.com.anteros.nosql.persistence.proxy;


import java.util.Collection;
import java.util.Map;

import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.session.NoSQLSession;

@SuppressWarnings("rawtypes")
public interface LazyProxyFactory {
	
    
	<T extends Collection> T createListProxy(final NoSQLSession session, T listToProxy, Class referenceObjClass, boolean ignoreMissing);

    <T extends Map> T createMapProxy(final NoSQLSession session, final T mapToProxy, final Class referenceObjClass,
                                     final boolean ignoreMissing);

    <T> T createProxy(final NoSQLSession session, Class<T> targetClass, final Key<T> key, final boolean ignoreMissing);

}
