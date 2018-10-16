package br.com.anteros.nosql.persistence.converters;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class ClassConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

    
    public ClassConverter() {
        super(Class.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object fromNoSQLObject, final NoSQLDescriptionField descriptionField) {
        if (fromNoSQLObject == null) {
            return null;
        }

        final String l = fromNoSQLObject.toString();
        try {
            return Class.forName(l);
        } catch (ClassNotFoundException e) {
            throw new NoSQLMappingException("Cannot create class from Name '" + l + "'", e);
        }
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        } else {
            return ((Class<?>) value).getName();
        }
    }
}
