package br.com.anteros.nosql.persistence.proxy;

import java.util.Map;

import br.com.anteros.nosql.persistence.converters.Key;

public interface ProxiedEntityReferenceMap extends ProxiedReference {
	// CHECKSTYLE:OFF
	Map<Object, Key<?>> __getReferenceMap();
	// CHECKSTYLE:ON

	// CHECKSTYLE:OFF
	void __put(Object key, Key<?> referenceKey);
	// CHECKSTYLE:ON
}
