package br.com.anteros.nosql.persistence.converters;


import java.util.List;

import br.com.anteros.core.utils.ListUtils;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class LongConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {


    public LongConverter() {
        super(long.class, Long.class, long[].class, Long[].class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
        if (val == null) {
            return null;
        }

        if (val instanceof Long) {
            return val;
        }

        if (val instanceof Number) {
            return ((Number) val).longValue();
        }

        if (val instanceof List) {
            final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
            return ListUtils.convertToArray(type, (List<?>) val);
        }

        return Long.parseLong(val.toString());
    }

}
