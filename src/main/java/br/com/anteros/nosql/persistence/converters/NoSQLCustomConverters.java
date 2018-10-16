package br.com.anteros.nosql.persistence.converters;


import static java.lang.String.format;

import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;


public class NoSQLCustomConverters extends NoSQLConverters {
    private final NoSQLConverters defaultConverters;


    public NoSQLCustomConverters(final AbstractNoSQLObjectMapper mapper, NoSQLDialect dialect) {
        super(mapper);
        defaultConverters = dialect.getDefaultConverters(mapper);
    }

    @Override
    public boolean isRegistered(final Class<? extends NoSQLTypeConverter> tcClass) {
        return super.isRegistered(tcClass) || defaultConverters.isRegistered(tcClass);
    }

    @Override
    public void removeConverter(final NoSQLTypeConverter tc) {
        super.removeConverter(tc);
        defaultConverters.removeConverter(tc);
    }

    @Override
    protected NoSQLTypeConverter getEncoder(final Class<?> c) {
        NoSQLTypeConverter encoder = super.getEncoder(c);
        if (encoder == null) {
            encoder = defaultConverters.getEncoder(c);
        }

        if (encoder != null) {
            return encoder;
        }
        throw new ConverterNotFoundException(format("Cannot find encoder for %s", c.getName()));
    }

    @Override
    protected NoSQLTypeConverter getEncoder(final Object val, final NoSQLDescriptionField descriptionField) {
        NoSQLTypeConverter encoder = super.getEncoder(val, descriptionField);
        if (encoder == null) {
            encoder = defaultConverters.getEncoder(val, descriptionField);
        }

        if (encoder != null) {
            return encoder;
        }

        throw new ConverterNotFoundException(format("Cannot find encoder for %s as need for %s", descriptionField.getField().getType(), descriptionField.getField().getName()));
    }
}
