package br.com.anteros.nosql.persistence.converters;

import static java.time.ZoneId.systemDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class LocalDateConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {
    /**
     * Creates the Converter.
     */
    public LocalDateConverter() {
        super(LocalDate.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
        if (val == null) {
            return null;
        }

        if (val instanceof LocalDate) {
            return val;
        }

        if (val instanceof Date) {
            return LocalDateTime.ofInstant(((Date) val).toInstant(), ZoneId.systemDefault()).toLocalDate();
        }

        throw new IllegalArgumentException("Can't convert to LocalDate from " + val);
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }
        LocalDate date = (LocalDate) value;
        return Date.from(date.atStartOfDay()
                             .atZone(systemDefault())
                             .toInstant());
    }
}
