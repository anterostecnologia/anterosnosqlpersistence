package br.com.anteros.nosql.persistence.converters;

import java.util.List;

import br.com.anteros.core.utils.ListUtils;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class IntegerConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

	public IntegerConverter() {
		super(int.class, Integer.class, int[].class, Integer[].class);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
		if (val == null) {
			return null;
		}

		if (val instanceof Integer) {
			return val;
		}

		if (val instanceof Number) {
			return ((Number) val).intValue();
		}

		if (val instanceof List) {
			final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
			return ListUtils.convertToArray(type, (List<?>) val);
		}

		return Integer.parseInt(val.toString());
	}
}
