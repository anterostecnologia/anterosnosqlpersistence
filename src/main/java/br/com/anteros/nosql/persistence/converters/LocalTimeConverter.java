package br.com.anteros.nosql.persistence.converters;


import java.time.LocalTime;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class LocalTimeConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

    private static final int MILLI_MODULO = 1000000;

   
    public LocalTimeConverter() {
        super(LocalTime.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
        if (val == null) {
            return null;
        }

        if (val instanceof LocalTime) {
            return val;
        }

        if (val instanceof Number) {
            return LocalTime.ofNanoOfDay(((Number) val).longValue() * MILLI_MODULO);
        }

        throw new IllegalArgumentException("Can't convert to LocalTime from " + val);
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }
        LocalTime time = (LocalTime) value;

        return time.toNanoOfDay() / MILLI_MODULO;
    }
}
