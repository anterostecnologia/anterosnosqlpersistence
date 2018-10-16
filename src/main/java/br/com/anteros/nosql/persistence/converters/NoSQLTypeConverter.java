package br.com.anteros.nosql.persistence.converters;


import java.util.Arrays;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;


public abstract class NoSQLTypeConverter {
    private AbstractNoSQLObjectMapper mapper;
    private Class<?>[] supportedTypes;

    protected NoSQLTypeConverter() {
    }

    protected NoSQLTypeConverter(final Class<?>... types) {
        supportedTypes = copy(types);
    }


    public final Object decode(final Class<?> targetClass, final Object fromNoSQLObject) {
        return decode(targetClass, fromNoSQLObject, null);
    }

    
    public abstract Object decode(final Class<?> targetClass, final Object fromNoSQLObject,final  NoSQLDescriptionField descriptionField);


    public final Object encode(final Object value) {
        return encode(value, null);
    }


    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        return value; 
    }

    public AbstractNoSQLObjectMapper getMapper() {
        return mapper;
    }

 
    public void setMapper(final AbstractNoSQLObjectMapper mapper) {
        this.mapper = mapper;
    }



    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }

  
    protected boolean isSupported(final Class<?> c, final NoSQLDescriptionField descriptionField) {
        return false;
    }

  
    protected boolean oneOf(final Class<?> f, final Class<?>... classes) {
        return oneOfClasses(f, classes);
    }

    protected boolean oneOfClasses(final Class<?> f, final Class<?>[] classes) {
        for (final Class<?> c : classes) {
            if (c.equals(f)) {
                return true;
            }
        }
        return false;
    }

    public Class<?>[] copy(final Class<?>[] array) {
        return array == null ? null : Arrays.copyOf(array, array.length);
    }

 
    public final Class<?>[] getSupportedTypes() {
        return copy(supportedTypes);
    }

  
    public void setSupportedTypes(final Class<?>[] supportedTypes) {
        this.supportedTypes = copy(supportedTypes);
    }

  
    public final boolean canHandle(final Class<?> c) {
        return isSupported(c, null);
    }

    public final boolean canHandle(final NoSQLDescriptionField descriptionField) {
        return isSupported(descriptionField.getType(), descriptionField);
    }
}
