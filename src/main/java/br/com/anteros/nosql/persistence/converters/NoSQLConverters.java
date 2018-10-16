package br.com.anteros.nosql.persistence.converters;


import static java.lang.String.format;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import br.com.anteros.core.log.LogLevel;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;
import br.com.anteros.nosql.persistence.session.mapping.NoSQLMapperOptions;


public abstract class NoSQLConverters {
	private static Logger LOG = LoggerProvider.getInstance().getLogger(NoSQLConverters.class);

    private final AbstractNoSQLObjectMapper mapper;
    private final List<NoSQLTypeConverter> untypedTypeEncoders = new LinkedList<>();
    private final Map<Class<?>, List<NoSQLTypeConverter>> tcMap = new ConcurrentHashMap<Class<?>, List<NoSQLTypeConverter>>();
    private final List<Class<? extends NoSQLTypeConverter>> registeredConverterClasses = new ArrayList<Class<? extends NoSQLTypeConverter>>();


    public NoSQLConverters(final AbstractNoSQLObjectMapper mapper) {
        this.mapper = mapper;
    }


    public NoSQLTypeConverter addConverter(final Class<? extends NoSQLTypeConverter> clazz) {
        return addConverter(mapper.getOptions().getObjectFactory().createInstance(clazz));
    }


    public NoSQLTypeConverter addConverter(final NoSQLTypeConverter tc) {
        if (tc.getSupportedTypes() != null) {
            for (final Class<?> c : tc.getSupportedTypes()) {
                addTypedConverter(c, tc);
            }
        } else {
            untypedTypeEncoders.add(tc);
        }

        registeredConverterClasses.add(tc.getClass());
        tc.setMapper(mapper);

        return tc;
    }


    public Object decode(final Class<?> c, final Object fromNoSQLObject, final NoSQLDescriptionField descriptionField) {
        Class<?> toDecode = c;
        if (toDecode == null) {
            toDecode = fromNoSQLObject.getClass();
        }
        return getEncoder(toDecode).decode(toDecode, fromNoSQLObject, descriptionField);
    }

    public Object encode(final Object o) {
        if (o == null) {
            return null;
        }
        return encode(o.getClass(), o);
    }

    public Object encode(final Class<?> c, final Object o) {
        return getEncoder(c).encode(o);
    }

    public void fromDocument(final Object noSQLObject, final NoSQLDescriptionField descriptionField, final Object targetEntity) {
        final Object object = descriptionField.getDbObjectValue(mapper.getDescriptionEntityManager().getDialect(), noSQLObject);
        if (object != null) {
            final NoSQLTypeConverter enc = getEncoder(descriptionField);
            final Object decodedValue = enc.decode(descriptionField.getField().getType(), object, descriptionField);
            try {
                descriptionField.setObjectValue(targetEntity, decodedValue);
            } catch (Exception e) {
                throw new NoSQLMappingException(format("Error setting value from converter (%s) for %s to %s",
                                                  enc.getClass().getSimpleName(), descriptionField.getFullName(), decodedValue), e);
			}
        }
    }


    public boolean hasDocumentConverter(final NoSQLDescriptionField field) {
        final NoSQLTypeConverter converter = getEncoder(field);
        return converter != null && !(converter instanceof IdentityConverter) && !(converter instanceof NoSQLSimpleValueConverter);
    }


    public boolean hasDocumentConverter(final Class<?> c) {
        final NoSQLTypeConverter converter = getEncoder(c);
        return converter != null && !(converter instanceof IdentityConverter) && !(converter instanceof NoSQLSimpleValueConverter);
    }


    public boolean hasSimpleValueConverter(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Class) {
            return hasSimpleValueConverter((Class<?>) o);
        } else if (o instanceof NoSQLDescriptionField) {
            return hasSimpleValueConverter((NoSQLDescriptionField) o);
        } else {
            return hasSimpleValueConverter(o.getClass());
        }
    }


    public boolean hasSimpleValueConverter(final Class<?> c) {
        return (getEncoder(c) instanceof NoSQLSimpleValueConverter);
    }


    public boolean hasSimpleValueConverter(final NoSQLDescriptionField c) {
        return (getEncoder(c) instanceof NoSQLSimpleValueConverter);
    }


    public boolean isRegistered(final Class<? extends NoSQLTypeConverter> tcClass) {
        return registeredConverterClasses.contains(tcClass);
    }


    public void removeConverter(final NoSQLTypeConverter tc) {
        if (tc.getSupportedTypes() == null) {
            untypedTypeEncoders.remove(tc);
            registeredConverterClasses.remove(tc.getClass());
        } else {
            for (final Entry<Class<?>, List<NoSQLTypeConverter>> entry : tcMap.entrySet()) {
                List<NoSQLTypeConverter> list = entry.getValue();
                if (list.contains(tc)) {
                    list.remove(tc);
                }
                if (list.isEmpty()) {
                    tcMap.remove(entry.getKey());
                }
            }
            registeredConverterClasses.remove(tc.getClass());
        }

    }


    public void toDocument(final Object containingObject, final NoSQLDescriptionField descriptionField, final Object noSQLObject, final NoSQLMapperOptions opts) throws Exception {
        final Object fieldValue = descriptionField.getObjectValue(containingObject);
        final NoSQLTypeConverter enc = getEncoder(fieldValue, descriptionField);

        final Object encoded = enc.encode(fieldValue, descriptionField);
        if (encoded != null || opts.isStoreNulls()) {
        	mapper.getDescriptionEntityManager().getDialect().setDbObjectValue(noSQLObject,descriptionField.getName(), encoded,descriptionField.isIdentifier());
        }
    }

    protected NoSQLTypeConverter getEncoder(final Class<?> c) {
        final List<NoSQLTypeConverter> tcs = tcMap.get(c);
        if (tcs != null) {
            if (tcs.size() > 1) {
            	LOG.log(LogLevel.WARN,"Duplicate converter for " + c + ", returning first one from " + tcs);
            }
            return tcs.get(0);
        }

        for (final NoSQLTypeConverter tc : untypedTypeEncoders) {
            if (tc.canHandle(c)) {
                return tc;
            }
        }

        return null;
    }

    protected NoSQLTypeConverter getEncoder(final Object val, final NoSQLDescriptionField descriptionField) {

        List<NoSQLTypeConverter> tcs = null;

        if (val != null) {
            tcs = tcMap.get(val.getClass());
        }

        if (tcs == null || (!tcs.isEmpty() && tcs.get(0) instanceof IdentityConverter)) {
            tcs = tcMap.get(descriptionField.getField().getType());
        }

        if (tcs != null) {
            if (tcs.size() > 1) {
            	LOG.log(LogLevel.WARN,"Duplicate converter for " + descriptionField.getField().getType() + ", returning first one from " + tcs);
            }
            return tcs.get(0);
        }

        for (final NoSQLTypeConverter tc : untypedTypeEncoders) {
            if (tc.canHandle(descriptionField) || (val != null && tc.isSupported(val.getClass(), descriptionField))) {
                return tc;
            }
        }

        return null;
    }

    private void addTypedConverter(final Class<?> type, final NoSQLTypeConverter tc) {
        if (tcMap.containsKey(type)) {
            tcMap.get(type).add(0, tc);
            LOG.log(LogLevel.WARN, "Added duplicate converter for " + type + " ; " + tcMap.get(type));
        } else {
            final List<NoSQLTypeConverter> values = new ArrayList<NoSQLTypeConverter>();
            values.add(tc);
            tcMap.put(type, values);
        }
    }

    private NoSQLTypeConverter getEncoder(final NoSQLDescriptionField descriptionField) {
        return getEncoder(null, descriptionField);
    }
}
