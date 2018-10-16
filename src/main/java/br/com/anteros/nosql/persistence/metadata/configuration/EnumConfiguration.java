package br.com.anteros.nosql.persistence.metadata.configuration;

import java.io.Serializable;

public class EnumConfiguration extends EntityConfiguration {

	public EnumConfiguration(Class<? extends Serializable> sourceClazz, NoSQLPersistenceModelConfiguration model) {
		super(sourceClazz, model);
	}


}
