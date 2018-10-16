package br.com.anteros.nosql.persistence.converters;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class IdentityConverter extends NoSQLTypeConverter {

	public IdentityConverter(final Class<?>... types) {
		super(types);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object fromNoSQLObject,
			final NoSQLDescriptionField descriptionField) {
		return fromNoSQLObject;
	}

	@Override
	public boolean isSupported(final Class<?> c, final NoSQLDescriptionField descriptionField) {
		return true;
	}
}
