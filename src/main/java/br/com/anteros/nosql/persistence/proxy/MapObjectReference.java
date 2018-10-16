package br.com.anteros.nosql.persistence.proxy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.session.NoSQLSession;

public class MapObjectReference extends AbstractReference implements ProxiedEntityReferenceMap {

	private static final long serialVersionUID = 1L;
	private final HashMap<Object, Key<?>> keyMap;

	public MapObjectReference(final NoSQLSession<?> session, final Map<?,?> mapToProxy, final Class<?> referenceObjClass,
			final boolean ignoreMissing) {

		super(session, referenceObjClass, ignoreMissing);
		object = mapToProxy;
		keyMap = new LinkedHashMap<Object, Key<?>>();
	}

	// CHECKSTYLE:OFF
	@Override
	public Map<Object, Key<?>> __getReferenceMap() {
		return keyMap;
	}
	// CHECKSTYLE:ON

	// CHECKSTYLE:OFF
	@Override
	public void __put(final Object key, final Key k) {
		keyMap.put(key, k);
	}

	@Override
	protected void beforeWriteObject() {
		if (__isFetched()) {
			syncKeys();
			((Map<?,?>) object).clear();
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object fetch() throws Exception {
		final Map m = (Map) object;
		m.clear();
		// TODO us: change to getting them all at once and yell according to
		// ignoreMissing in order to a) increase performance and b) resolve
		// equals keys to the same instance
		// that should really be done in datastore.
		for (final Map.Entry<?, Key<?>> e : keyMap.entrySet()) {
			final Key<?> entityKey = e.getValue();
			m.put(e.getKey(), fetch(entityKey));
		}
		return m;
	}

	@SuppressWarnings("unchecked")
	private void syncKeys() {
		final NoSQLSession<?> ds = getSession();

		keyMap.clear();
		final Map<Object, Object> map = (Map<Object,Object>) object;
		for (final Map.Entry<Object, Object> e : map.entrySet()) {
			keyMap.put(e.getKey(), ds.getKey(e.getValue()));
		}
	}

}
