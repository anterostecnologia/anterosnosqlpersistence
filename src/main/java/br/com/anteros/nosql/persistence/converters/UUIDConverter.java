package br.com.anteros.nosql.persistence.converters;

import java.util.UUID;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class UUIDConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

	public UUIDConverter() {
		super(UUID.class);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object fromNoSQLObject,
			final NoSQLDescriptionField descriptionField) {
		return fromNoSQLObject == null ? null : UUID.fromString((String) fromNoSQLObject);
	}

	@Override
	public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
		return value == null ? null : value.toString();
	}
}
