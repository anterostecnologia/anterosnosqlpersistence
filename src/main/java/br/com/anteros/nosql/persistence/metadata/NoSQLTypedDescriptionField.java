package br.com.anteros.nosql.persistence.metadata;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;

public class NoSQLTypedDescriptionField extends NoSQLDescriptionField {

	private NoSQLDescriptionField parent;
	private ParameterizedType pType;
	private Object value;

	public NoSQLTypedDescriptionField(final ParameterizedType t, final NoSQLDescriptionField descriptionField) {
        super(descriptionField.getDescriptionEntity(), descriptionField.getField(), t);
        parent = descriptionField;
        pType = t;
        final Class rawClass = (Class) t.getRawType();
        if (ReflectionUtils.isImplementsInterface(rawClass, Set.class)) {
        	this.fieldType = FieldType.ARRAY_OR_COLLECTION;
        }
        if (ReflectionUtils.isImplementsInterface(rawClass, Map.class)) {
        	this.fieldType = FieldType.MAP;
        }
        
        setMapKeyType(getMapKeyClass());
        setSubType(getSubType());
        setBoDatabaseType(descriptionField.getDescriptionEntity().getDialect().isDatabaseType(getSubClass()));
    }
	
	public NoSQLTypedDescriptionField(final Type t, final NoSQLDescriptionField descriptionField) {
		super(descriptionField.getDescriptionEntity(), descriptionField.getField(), t);
        parent = descriptionField;
    }
	
	@Override
	public String getName() {
		return "fake";
	}
	
	@Override
    public Class getMapKeyClass() {
        return (Class) (isAnyMap() ? pType.getActualTypeArguments()[0] : null);
    }
	
	@Override
    public Class getSubClass() {
        return toClass(getSubType());
    }

    @Override
    public Type getSubType() {
        return pType != null ? pType.getActualTypeArguments()[isAnyMap() ? 1 : 0] : null;
    }

    @Override
    public Class getType() {
        if (pType == null) {
            return super.getType();
        } else if (isAnyMap()) {
            return Map.class;
        } else {
            return List.class;
        }
    }

	public NoSQLDescriptionField getParent() {
		return parent;
	}
	
	@Override
	public Object getDbObjectValue(NoSQLDialect dialect, Object noSQLObject) {
		return noSQLObject;
	}
	
	@Override
    public Object getObjectValue(final Object instance) {
        return value;
    }
	
	@Override
    public void setObjectValue(final Object instance, final Object val) {
        this.value = val;
    }

	public Object getValue() {
		return value;
	}

}
