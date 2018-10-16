package br.com.anteros.nosql.persistence.converters;


import static java.time.ZoneId.systemDefault;

import java.time.LocalDateTime;
import java.util.Date;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class LocalDateTimeConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

 
    public LocalDateTimeConverter() {
        super(LocalDateTime.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
        if (val == null) {
            return null;
        }

        if (val instanceof LocalDateTime) {
            return val;
        }

        if (val instanceof Date) {
            return LocalDateTime.ofInstant(((Date) val).toInstant(), systemDefault());
        }

        throw new IllegalArgumentException("Can't convert to LocalDateTime from " + val);
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }
        return Date.from(((LocalDateTime) value).atZone(systemDefault()).toInstant());
    }
}
