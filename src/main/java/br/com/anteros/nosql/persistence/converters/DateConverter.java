package br.com.anteros.nosql.persistence.converters;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;



public class DateConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {
	private static Logger LOG = LoggerProvider.getInstance().getLogger(DateConverter.class);


    public DateConverter() {
        this(Date.class);
    }

    protected DateConverter(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            return val;
        }

        if (val instanceof Number) {
            return new Date(((Number) val).longValue());
        }

        if (val instanceof String) {
            try {
                return new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.US).parse((String) val);
            } catch (ParseException e) {
                LOG.error("Can't parse Date from: " + val);
            }

        }

        throw new IllegalArgumentException("Can't convert to Date from " + val);
    }
}
