package br.com.anteros.nosql.persistence.converters;



import java.lang.reflect.Array;
import java.util.List;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;



public class ShortConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

    public ShortConverter() {
        super(short.class, Short.class, short[].class, Short[].class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
        if (val == null) {
            return null;
        }

        if (val instanceof Short) {
            return val;
        }

        if (val instanceof Number) {
            return ((Number) val).shortValue();
        }

        if (val instanceof List) {
            final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
            return convertToArray(type, (List<?>) val);
        }

        return Short.parseShort(val.toString());
    }

    Object convertToArray(final Class<?> type, final List<?> values) {
        final Object array = Array.newInstance(type, values.size());
        for (int i = 0; i < values.size(); i++) {
            Array.set(array, i, ((Number) values.get(i)).shortValue());
        }
        return array;
    }

}
