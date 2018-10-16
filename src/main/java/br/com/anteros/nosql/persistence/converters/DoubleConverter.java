package br.com.anteros.nosql.persistence.converters;



import java.util.List;

import br.com.anteros.core.utils.ListUtils;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class DoubleConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

 
    public DoubleConverter() {
        super(double.class, Double.class, double[].class, Double[].class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
        if (val == null) {
            return null;
        }

        if (val instanceof Double) {
            return val;
        }

        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }

        if (val instanceof List) {
            final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
            return ListUtils.convertToArray(type, (List<?>) val);
        }

        return Double.parseDouble(val.toString());
    }
}
