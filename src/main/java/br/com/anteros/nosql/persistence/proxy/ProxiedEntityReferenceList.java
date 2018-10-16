package br.com.anteros.nosql.persistence.proxy;

import java.util.Collection;
import java.util.List;

import br.com.anteros.nosql.persistence.converters.Key;

public interface ProxiedEntityReferenceList extends ProxiedReference, AnterosPersistentCollection {
	// CHECKSTYLE:OFF
	void __add(Key<?> key);

	void __addAll(Collection<? extends Key<?>> keys);

	List<Key<?>> __getKeysAsList();
	// CHECKSTYLE:ON
}
