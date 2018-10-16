package br.com.anteros.nosql.persistence.converters;

import java.util.LinkedHashMap;
import java.util.Map;

import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.nosql.persistence.converters.IterHelper.MapIterCallback;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;


public class MapOfValuesConverter extends NoSQLTypeConverter {
	
    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class<?> targetClass, final Object fromNoSQLObject, final NoSQLDescriptionField descriptionField) {
        if (fromNoSQLObject == null) {
            return null;
        }

        final Map values = getMapper().getOptions().getObjectFactory().createMap(descriptionField);
        new IterHelper<Object, Object>().loopMap(fromNoSQLObject, new MapIterCallback<Object, Object>() {
            @Override
            public void eval(final Object k, final Object val) {
                final Object objKey = getMapper().getConverters().decode(descriptionField.getMapKeyClass(), k, descriptionField);
                
                Object decodedValue = getMapper().getConverters().decode(descriptionField.getSubClass(), val, descriptionField);
                values.put(objKey, val != null ? decodedValue : null);
            }
        });

        return values;
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }

        final Map<?, ?> map = (Map<?, ?>) value;
        if (!map.isEmpty() || getMapper().getOptions().isStoreEmpties()) {
            final Map<Object, Object> mapForDb = new LinkedHashMap<Object, Object>();
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                final String strKey = getMapper().getConverters().encode(entry.getKey()).toString();
                mapForDb.put(strKey, getMapper().getConverters().encode(entry.getValue()));
            }
            return mapForDb;
        }
        return null;
    }

    @Override
    protected boolean isSupported(final Class<?> c, final NoSQLDescriptionField descriptionField) {
        if (descriptionField != null) {
            return descriptionField.isAnyMap();
        } else {
            return ReflectionUtils.isImplementsInterface(c, Map.class);
        }
    }
}
