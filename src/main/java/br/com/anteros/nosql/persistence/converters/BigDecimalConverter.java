package br.com.anteros.nosql.persistence.converters;


import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class BigDecimalConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {


    public BigDecimalConverter() {
        super(BigDecimal.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal(((BigInteger) value));
        }

        if (value instanceof Double) {
            return new BigDecimal(((Double) value));
        }

        if (value instanceof Long) {
            return new BigDecimal(((Long) value));
        }

        if (value instanceof Number) {
            return new BigDecimal(((Number) value).doubleValue());
        }

        return new BigDecimal(value.toString());
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        return super.encode(value, descriptionField);
    }
}
