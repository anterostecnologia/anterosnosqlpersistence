package br.com.anteros.nosql.persistence.converters;

import java.util.List;

import br.com.anteros.core.utils.ListUtils;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class StringConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

	public StringConverter() {
		super(String.class, String[].class);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object fromNoSQLObject,
			final NoSQLDescriptionField descriptionField) {
		if (fromNoSQLObject == null) {
			return null;
		}

		if (targetClass.equals(fromNoSQLObject.getClass())) {
			return fromNoSQLObject;
		}

		if (fromNoSQLObject instanceof List) {
			final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
			return ListUtils.convertToArray(type, (List<?>) fromNoSQLObject);
		}

		if (targetClass.equals(String[].class)) {
			return new String[] { fromNoSQLObject.toString() };
		}

		return fromNoSQLObject.toString();
	}
}
