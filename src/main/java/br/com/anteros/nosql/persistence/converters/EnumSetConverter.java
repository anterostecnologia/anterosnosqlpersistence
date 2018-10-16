package br.com.anteros.nosql.persistence.converters;



import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class EnumSetConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

    private final EnumConverter ec = new EnumConverter();


    public EnumSetConverter() {
        super(EnumSet.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class<?> targetClass, final Object fromNoSQLObject, final NoSQLDescriptionField descriptionField) {
        if (fromNoSQLObject == null) {
            return null;
        }

        final Class enumType = (Class) descriptionField.getGenericType();

        final List<?> l = (List<?>) fromNoSQLObject;
        if (l.isEmpty()) {
            return EnumSet.noneOf(enumType);
        }

        final List enums = new ArrayList<>();
        for (final Object object : l) {
            enums.add(ec.decode(enumType, object));
        }
        return EnumSet.copyOf(enums);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }

        final List values = new ArrayList();

        final EnumSet s = (EnumSet) value;
        final Object[] array = s.toArray();
        for (final Object anArray : array) {
            values.add(ec.encode(anArray));
        }

        return values;
    }
}
