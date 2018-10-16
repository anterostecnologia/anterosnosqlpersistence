package br.com.anteros.nosql.persistence.converters;

import java.time.Instant;
import java.util.Date;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class InstantConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

	public InstantConverter() {
		super(Instant.class);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
		if (val == null) {
			return null;
		}

		if (val instanceof Instant) {
			return val;
		}

		if (val instanceof Date) {
			return ((Date) val).toInstant();
		}

		throw new IllegalArgumentException("Can't convert to Instant from " + val);
	}

	@Override
	public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
		if (value == null) {
			return null;
		}
		return Date.from((Instant) value);
	}
}
