package br.com.anteros.nosql.persistence.proxy;

import static java.lang.String.format;

import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.session.NoSQLSession;

public class EntityObjectReference extends AbstractReference implements ProxiedEntityReference {
	private static final long serialVersionUID = 1L;
	private final Key<?> key;

	public EntityObjectReference(final NoSQLSession<?> session, final Class<?> targetClass, final Key<?> key,
			final boolean ignoreMissing) {
		super(session, targetClass, ignoreMissing);
		this.key = key;
	}

	// CHECKSTYLE:OFF
	@Override
	public Key<?> __getKey() {
		return key;
	}
	// CHECKSTYLE:ON

	@Override
	protected void beforeWriteObject() {
		object = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object fetch() throws Exception {
		final Object entity = getSession().findById(key.getId(), referenceObjClass);
		if (entity == null && !ignoreMissing) {
			throw new LazyReferenceFetchingException(
					format("During the lifetime of the proxy, the Entity identified by '%s' "
							+ "disappeared from the Datastore.", key));
		}
		return entity;
	}
}
