package br.com.anteros.nosql.persistence.converters;



import java.util.List;

import br.com.anteros.core.utils.ListUtils;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;



public class BooleanConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

 
    public BooleanConverter() {
        super(boolean.class, Boolean.class, boolean[].class, Boolean[].class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField optionalExtraInfo) {
        if (val == null) {
            return null;
        }

        if (val instanceof Boolean) {
            return val;
        }

        //handle the case for things like the ok field
        if (val instanceof Number) {
            return ((Number) val).intValue() != 0;
        }

        if (val instanceof List) {
            final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
            return ListUtils.convertToArray(type, (List<?>) val);
        }

        return Boolean.parseBoolean(val.toString());
    }
}
