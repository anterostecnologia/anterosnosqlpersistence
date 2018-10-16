package br.com.anteros.nosql.persistence.converters;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class EnumConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Object decode(final Class targetClass, final Object fromNoSQLObject, final NoSQLDescriptionField descriptionField) {
        if (fromNoSQLObject == null) {
            return null;
        }
        return Enum.valueOf(targetClass, fromNoSQLObject.toString());
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }

        return getName((Enum<?>) value);
    }

    @Override
    protected boolean isSupported(final Class<?> c, final NoSQLDescriptionField descriptionField) {
        return c.isEnum();
    }

    private <T extends Enum<?>> String getName(final T value) {
        return value.name();
    }
}
