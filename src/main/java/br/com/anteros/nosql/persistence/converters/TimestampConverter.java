package br.com.anteros.nosql.persistence.converters;

import java.sql.Timestamp;
import java.util.Date;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class TimestampConverter extends DateConverter {

	public TimestampConverter() {
		super(Timestamp.class);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
		final Date d = (Date) super.decode(targetClass, val, descriptionField);
		return new Timestamp(d.getTime());
	}

	@Override
	public Object encode(final Object val, final NoSQLDescriptionField descriptionField) {
		if (val == null) {
			return null;
		}
		return new Date(((Timestamp) val).getTime());
	}
}
