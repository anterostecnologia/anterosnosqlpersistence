package br.com.anteros.nosql.persistence.session.mapping;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.session.cache.NoSQLEntityCacheFactory;

public abstract class NoSQLMapperOptions {
	
	protected NoSQLDescriptionEntityManager descriptionEntityManager;

	public NoSQLMapperOptions(NoSQLDescriptionEntityManager descriptionEntityManager) {
		this.descriptionEntityManager = descriptionEntityManager;
	}

	public abstract NoSQLCustomMapper getDefaultMapper();

	public abstract NoSQLCustomMapper getEmbeddedMapper();

	public abstract NoSQLObjectFactory getObjectFactory();

	public abstract NoSQLCustomMapper getReferenceMapper();

	public abstract NoSQLCustomMapper getValueMapper();
	
	public abstract NoSQLEntityCacheFactory getCacheFactory();
	
	public abstract NoSQLMapperOptions defaultCreator(NoSQLObjectFactory defaultCreator);

	public abstract NoSQLMapperOptions defaultMapper(NoSQLCustomMapper defaultMapper);

	public abstract NoSQLMapperOptions embeddedMapper(NoSQLCustomMapper embeddedMapper);

	public abstract NoSQLMapperOptions referenceMapper(NoSQLCustomMapper referenceMapper);

	public abstract NoSQLMapperOptions valueMapper(NoSQLCustomMapper valueMapper);

	public abstract NoSQLMapperOptions cacheFactory(NoSQLEntityCacheFactory cacheFactory);
	
	public abstract NoSQLMapperOptions cacheClassLookups(boolean cacheClassLookups);

	public abstract NoSQLMapperOptions ignoreFinals(boolean ignoreFinals);

	public abstract NoSQLMapperOptions storeEmpties(boolean storeEmpties);

	public abstract NoSQLMapperOptions storeNulls(boolean storeNulls);

	public abstract NoSQLMapperOptions useLowerCaseCollectionNames(boolean useLowerCaseCollectionNames);

	public abstract boolean isCacheClassLookups();

	public abstract boolean isIgnoreFinals();

	public abstract boolean isStoreEmpties();

	public abstract boolean isStoreNulls();

	public abstract boolean isUseLowerCaseCollectionNames();

}
