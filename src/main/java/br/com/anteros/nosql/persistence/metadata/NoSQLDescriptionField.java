package br.com.anteros.nosql.persistence.metadata;

import static java.lang.String.format;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.core.log.LogLevel;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.utils.ObjectUtils;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.nosql.persistence.converters.NoSQLMappingException;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.accessor.PropertyAccessor;
import br.com.anteros.nosql.persistence.metadata.annotations.type.CascadeType;
import br.com.anteros.nosql.persistence.metadata.annotations.type.TemporalType;
import br.com.anteros.nosql.persistence.session.NoSQLSession;

public class NoSQLDescriptionField {

	protected static Logger LOG = LoggerProvider.getInstance().getLogger(NoSQLDescriptionField.class);

	protected NoSQLDescriptionEntity descriptionEntity;
	protected Field field;
	protected String name;
	protected FieldType fieldType = FieldType.SIMPLE;
	protected PropertyAccessor propertyAccessor = null;
	protected TemporalType temporalType;
	protected CascadeType[] cascadeTypes = { CascadeType.NONE };
	protected List<NoSQLDescriptionIndex> indexes = new ArrayList<NoSQLDescriptionIndex>();
	private final List<NoSQLTypedDescriptionField> typeParameters = new ArrayList<>();
	protected boolean boReference = false;
	protected boolean boLob = false;
	protected boolean boEmbedded = false;
	protected boolean boIdentifier = false;
	protected boolean boEnumerated = false;
	protected boolean boVersioned = false;
	protected boolean boRequired = false;
	protected String collectionName;
	protected Map<String, String> enumValues;
	protected String defaultValue = "";
	protected Class realType; // the real type
	protected Type subType; // the type (T) for the Collection<T>/T[]/Map<?,T>
	protected Type mapKeyType; // the type (T) for the Map<T,?>
	protected boolean boSerialized;
	protected boolean boDisableCompression;
	protected boolean idOnlyReference;
	protected boolean boLazyLoadReference;
	protected boolean boIgnoreMissingReference;
	protected boolean boNotSaved;
	protected boolean boDatabaseType;
	protected Class<?> concreteClassEmbedded;
	protected Class<?> concreteClassProperty;
	protected boolean boProperty;
	protected Type genericType;
	protected Constructor constructor; // the constructor for the type
	protected String[] constructorArgs = {};
	protected NoSQLDescriptionMappedBy mappedBy;

	private NoSQLDescriptionEntity targetEntity;

	public NoSQLDescriptionField(NoSQLDescriptionEntity descriptionEntity, Field field) {
		setField(field);
		this.descriptionEntity = descriptionEntity;

		realType = field.getType();
		genericType = field.getGenericType();
		discoverAllTypes();

	}

	public NoSQLDescriptionField(NoSQLDescriptionEntity descriptionEntity, Field field, Type type) {
		setField(field);
		this.descriptionEntity = descriptionEntity;
		genericType = type;
		discoverAllTypes();

	}

	protected void discoverAllTypes() {
		discoverType();
		discoverMultivalued();
		discoverDatabaseType();
		discoverConstructor();
	}

	private void discoverDatabaseType() {
		boDatabaseType = descriptionEntity.getDialect().isDatabaseType(realType);

		// if the main type isn't supported by the Mongo, see if the subtype is.
		// works for T[], List<T>, Map<?, T>, where T is Long/String/etc.
		if (!boDatabaseType && subType != null) {
			boDatabaseType = descriptionEntity.getDialect().isDatabaseType(subType);
		}

		if (!boDatabaseType && !isSimple() && (subType == null || subType == Object.class)) {
			boDatabaseType = true;
		}

	}

	private void discoverMultivalued() {
		if (realType.isArray() || Collection.class.isAssignableFrom(realType) || Map.class.isAssignableFrom(realType)
				|| GenericArrayType.class.isAssignableFrom(genericType.getClass())) {

			if (Map.class.isAssignableFrom(realType)) {
				setFieldType(FieldType.MAP);
			}
			if (Set.class.isAssignableFrom(realType) || Collection.class.isAssignableFrom(realType)
					|| realType.isArray()) {
				setFieldType(FieldType.ARRAY_OR_COLLECTION);
			}
			// get the subtype T, T[]/List<T>/Map<?,T>; subtype of Long[], List<Long> is
			// Long
			subType = (realType.isArray()) ? realType.getComponentType()
					: ReflectionUtils.getParameterizedType(field, isAnyMap() ? 1 : 0);

			if (isAnyMap()) {
				mapKeyType = ReflectionUtils.getParameterizedType(field, 0);
			}
		}
	}

	protected void discoverType() {
		if (genericType instanceof TypeVariable) {
			realType = extractTypeVariable((TypeVariable<?>) genericType);
		} else if (genericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericType;
			final Type[] types = pt.getActualTypeArguments();
			realType = toClass(pt);

			for (Type type : types) {
				if (type instanceof ParameterizedType) {
					typeParameters.add(new NoSQLTypedDescriptionField((ParameterizedType) type, this));
				} else {
					if (type instanceof WildcardType) {
						type = ((WildcardType) type).getUpperBounds()[0];
					}
					typeParameters.add(new NoSQLTypedDescriptionField(type, this));
				}
			}
		} else if (genericType instanceof WildcardType) {
			final WildcardType wildcardType = (WildcardType) genericType;
			final Type[] types = wildcardType.getUpperBounds();
			realType = toClass(types[0]);
		} else if (genericType instanceof Class) {
			realType = (Class<?>) genericType;
		} else if (genericType instanceof GenericArrayType) {
			final Type genericComponentType = ((GenericArrayType) genericType).getGenericComponentType();
			if (genericComponentType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) genericComponentType;
				realType = toClass(genericType);

				final Type[] types = pt.getActualTypeArguments();
				for (Type type : types) {
					if (type instanceof ParameterizedType) {
						typeParameters.add(new NoSQLTypedDescriptionField((ParameterizedType) type, this));
					} else {
						if (type instanceof WildcardType) {
							type = ((WildcardType) type).getUpperBounds()[0];
						}
						typeParameters.add(new NoSQLTypedDescriptionField(type, this));
					}
				}
			} else {
				if (genericComponentType instanceof TypeVariable) {
					realType = toClass(genericType);
				} else {
					realType = (Class<?>) genericComponentType;
				}
			}
		}

		if (Object.class.equals(realType) || Object[].class.equals(realType)) {
			if (LOG.isWarnEnabled()) {
				LOG.log(LogLevel.WARN,
						format("Parameterized types are treated as untyped Objects. See field '%s' on %s",
								field.getName(), field.getDeclaringClass()));
			}
		}

		if (realType == null) {
			throw new NoSQLMappingException(
					format("A type could not be found for the field %s.%s", getField().getType(), getField()));
		}
	}

	private void discoverConstructor() {
		Class<?> type = null;
		if (concreteClassEmbedded != null && !(concreteClassEmbedded.equals(Object.class))) {
			type = concreteClassEmbedded;
		}

		if (concreteClassProperty != null && !(concreteClassProperty.equals(Object.class))) {
			type = concreteClassProperty;
		}

		if (type != null) {
			try {
				constructor = type.getDeclaredConstructor();
				constructor.setAccessible(true);
			} catch (NoSuchMethodException e) {
				if (constructorArgs == null || constructorArgs.length == 0) {
					if (LOG.isWarnEnabled()) {
						LOG.log(LogLevel.WARN, "No usable constructor for " + type.getName(), e);
					}
				}
			}
		} else {
			type = getType();

			if (type == List.class || type == Map.class) {
			} else if (type != null) {
				try {
					constructor = type.getDeclaredConstructor();
					constructor.setAccessible(true);
				} catch (NoSuchMethodException e) {
				} catch (SecurityException e) {
				}
			}
		}
	}

	public Class<?> getType() {
		return realType;
	}
	
	public Class<?> getTargetClass(){
		if (fieldType==FieldType.ARRAY_OR_COLLECTION) {
			return getSubClass();
		} else if (fieldType==FieldType.SIMPLE) {
			return getRealType();
		}
		return null;
	}

	private Class<?> extractTypeVariable(final TypeVariable<?> type) {
		final Class<?> typeArgument = ReflectionUtils.getTypeArgument(descriptionEntity.getEntityClass(), type);
		return typeArgument != null ? typeArgument : Object.class;
	}

	public NoSQLFieldEntityValue getFieldEntityValue(NoSQLSession session, Object object) throws Exception {
		Object fieldValue = this.getObjectValue(object);
		return getFieldEntityValue(session, object, fieldValue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NoSQLFieldEntityValue getFieldEntityValue(NoSQLSession session, Object object, Object fieldValue)
			throws Exception {
		if (this.isSimple()) {
			Map<String, Object> tempSimple = new LinkedHashMap<String, Object>();
			tempSimple.put(this.name, ObjectUtils.cloneObject(fieldValue));
			return new NoSQLFieldEntityValue(this.getField().getName(), tempSimple, object);
		}
		if (this.isAnyArrayOrCollection()) {
			List listTemp = new ArrayList();
			if ((fieldValue != null) && this.isInitialized(session, fieldValue)) {
				Iterator it = ((Collection) fieldValue).iterator();
				Object value;
				Map<String, Object> tempSimple;
				while (it.hasNext()) {
					value = it.next();
					tempSimple = new LinkedHashMap<String, Object>();
					Object newValue = ObjectUtils.cloneObject(value);
					if (this.isReferenced()) {
						newValue = ObjectUtils.cloneObject(session.getIdentifier(value));
					}

					tempSimple.put(this.name, newValue);
					listTemp.add(new NoSQLFieldEntityValue(this.getField().getName(), tempSimple, value));
				}
			}
			return new NoSQLFieldEntityValue(this.getField().getName(),
					listTemp.toArray(new NoSQLFieldEntityValue[] {}), ((Collection) fieldValue));
		} else if (this.isAnyMap()) {
			List listTemp = new ArrayList();
			if ((fieldValue != null) && this.isInitialized(session, fieldValue)) {
				Iterator it = ((Map) fieldValue).keySet().iterator();
				Object value;
				Object key;
				Map<Object, Object> tempSimple;
				while (it.hasNext()) {
					key = it.next();
					value = ((Map) fieldValue).get(key);

					Object newValue = ObjectUtils.cloneObject(value);

					if (this.isReferenced()) {
						newValue = ObjectUtils.cloneObject(session.getIdentifier(value));
					}

					tempSimple = new LinkedHashMap<Object, Object>();
					tempSimple.put(ObjectUtils.cloneObject(key), ObjectUtils.cloneObject(value));
					listTemp.add(new NoSQLFieldEntityValue(this.getField().getName(), newValue, key));
				}
			}
			return new NoSQLFieldEntityValue(this.getField().getName(),
					listTemp.toArray(new NoSQLFieldEntityValue[] {}), ((Map) fieldValue));
		} else if (this.isReferenced()) {
			Map<String, Object> tempSimple = new LinkedHashMap<String, Object>();
			tempSimple.put(this.name, ObjectUtils.cloneObject(session.getIdentifier(fieldValue)));
			return new NoSQLFieldEntityValue(this.getField().getName(), tempSimple, fieldValue);
		} else if (this.isEmbedded()) {
			Map<String, Object> tempSimple = new LinkedHashMap<String, Object>();
			tempSimple.put(this.name, fieldValue);
			return new NoSQLFieldEntityValue(this.getField().getName(), tempSimple, ObjectUtils.cloneObject(object));
		}
		return null;
	}

	public boolean isInitialized(NoSQLSession session, Object object) throws Exception {
		if (session.isProxyObject(object)) {
			return session.proxyIsInitialized(object);
		}
		return true;
	}

	public Object getObjectValue(Object object) {
		try {
			if (propertyAccessor != null) {
				return propertyAccessor.get(object);
			} else {
				return field.get(object);
			}
		} catch (Exception e) {
			throw new NoSQLDescriptionFieldException(e);
		}
	}

	public void setObjectValue(Object source, Object value) {
		try {
			if (propertyAccessor != null) {
				propertyAccessor.set(source, value);
			} else {
				field.set(source, value);
			}
		} catch (Exception e) {
			throw new NoSQLDescriptionFieldException(e);
		}
	}

	public Object getDbObjectValue(NoSQLDialect dialect, final Object noSQLObject) {
		return dialect.getDbObjectValue(this.getName(), noSQLObject, isIdentifier());
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		this.field.setAccessible(true);
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public NoSQLDescriptionEntity getDescriptionEntity() {
		return descriptionEntity;
	}

	public void setDescriptionEntity(NoSQLDescriptionEntity descriptionEntity) {
		this.descriptionEntity = descriptionEntity;
	}

	public boolean isAnyArrayOrCollectionOrMap() {
		return this.fieldType == FieldType.ARRAY_OR_COLLECTION || this.isAnyMap();
	}

	public boolean isAnyArrayOrCollection() {
		return this.fieldType == FieldType.ARRAY_OR_COLLECTION;
	}

	public boolean isAnyMap() {
		return this.fieldType == FieldType.MAP;
	}

	public boolean isSet() {
		return this.isAnyArrayOrCollection() && ReflectionUtils.isImplementsInterface(getType(), Set.class);
	}

	public boolean isList() {
		return this.isAnyArrayOrCollection() && ReflectionUtils.isImplementsInterface(getType(), List.class);
	}

	public boolean isReferenced() {
		return this.boReference;
	}

	public boolean isEmbedded() {
		return this.boEmbedded;
	}

	public boolean isSimple() {
		return this.fieldType == FieldType.SIMPLE;
	}

	public boolean isLob() {
		return this.boLob;
	}

	public boolean isString() {
		return (this.getField().getType() == String.class);
	}

	public boolean isTemporalDate() {
		return this.temporalType == TemporalType.DATE;
	}

	public boolean isTemporalDateTime() {
		return this.temporalType == TemporalType.DATE_TIME;
	}

	public boolean isTemporalTime() {
		return temporalType == TemporalType.TIME;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertyAccessor getPropertyAccessor() {
		return propertyAccessor;
	}

	public void setPropertyAccessor(PropertyAccessor propertyAccessor) {
		this.propertyAccessor = propertyAccessor;
	}

	public TemporalType getTemporalType() {
		return temporalType;
	}

	public void setTemporalType(TemporalType temporalType) {
		this.temporalType = temporalType;
	}

	public CascadeType[] getCascadeTypes() {
		return cascadeTypes;
	}

	public void setCascadeTypes(CascadeType[] cascadeTypes) {
		this.cascadeTypes = cascadeTypes;
	}

	public List<NoSQLDescriptionIndex> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<NoSQLDescriptionIndex> indexes) {
		this.indexes = indexes;
	}

	public boolean isIdentifier() {
		return boIdentifier;
	}

	public void setBoIdentifier(boolean boIdentifier) {
		this.boIdentifier = boIdentifier;
	}

	public String getCollectionName() {
		if (StringUtils.isEmpty(collectionName)) {
			return descriptionEntity.getCollectionName();
		}
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getValueEnum(String key) {
		return this.enumValues.get(key);
	}

	public void setEnumValues(Map<String, String> enumValues) {
		this.enumValues = enumValues;
	}

	public Map<String, String> getEnumValues() {
		return enumValues;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isBoEnumerated() {
		return boEnumerated;
	}

	public void setBoEnumerated(boolean boEnumerated) {
		this.boEnumerated = boEnumerated;
	}

	public void setVersioned(boolean value) {
		this.boVersioned = value;
	}

	public boolean isVersioned() {
		return this.boVersioned;
	}

	public void setBoLob(boolean boLob) {
		this.boLob = boLob;
	}

	public boolean isRequired() {
		return boRequired;
	}

	public void setBoRequired(boolean boRequired) {
		this.boRequired = boRequired;
	}

	public String getFullName() {
		return field.getDeclaringClass().getName() + "." + field.getName();
	}

	public Class<?> getMapKeyClass() {
		if (isAnyMap()) {
			mapKeyType = ReflectionUtils.getParameterizedType(field, 0);
		}
		return toClass(mapKeyType);
	}

	public Class<?> getSubClass() {
		if (subType == null)
			subType = (field.getType().isArray()) ? field.getType().getComponentType()
					: ReflectionUtils.getParameterizedType(field, isAnyMap() ? 1 : 0);
		return toClass(subType);
	}

	protected Class toClass(final Type t) {
		if (t == null) {
			return null;
		} else if (t instanceof Class) {
			return (Class) t;
		} else if (t instanceof GenericArrayType) {
			final Type type = ((GenericArrayType) t).getGenericComponentType();
			Class aClass;
			if (type instanceof ParameterizedType) {
				aClass = (Class) ((ParameterizedType) type).getRawType();
			} else if (type instanceof TypeVariable) {
				aClass = ReflectionUtils.getTypeArgument(descriptionEntity.getEntityClass(), (TypeVariable<?>) type);
				if (aClass == null) {
					aClass = Object.class;
				}
			} else {
				aClass = (Class) type;
			}
			return Array.newInstance(aClass, 0).getClass();
		} else if (t instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) t).getRawType();
		} else if (t instanceof WildcardType) {
			return (Class) ((WildcardType) t).getUpperBounds()[0];
		}

		throw new RuntimeException("Generic TypeVariable not supported!");

	}

	public boolean isArray() {
		return field.getType().isArray();
	}

	public void setBoSerialized(boolean value) {
		this.boSerialized = value;
	}

	public boolean isSerialized() {
		return this.boSerialized;
	}

	public void setDisableCompression(boolean disableCompression) {
		this.boDisableCompression = disableCompression;
	}

	public boolean isDisableCompression() {
		return this.boDisableCompression;
	}

	public void setIdOnlyReference(boolean idOnlyReference) {
		this.idOnlyReference = idOnlyReference;

	}

	public void setBoLazyLoadReference(boolean lazyLoadReference) {
		this.boLazyLoadReference = lazyLoadReference;

	}

	public void setBoIgnoreMissingReference(boolean ignoreMissingReference) {
		this.boIgnoreMissingReference = ignoreMissingReference;
	}

	public boolean isLazyLoadReference() {
		return this.boLazyLoadReference;
	}

	public boolean isIgnoreMissingReference() {
		return this.boIgnoreMissingReference;
	}

	public boolean isIdOnlyReference() {
		return this.idOnlyReference;
	}

	public void setBoNotSaved(boolean value) {
		this.boNotSaved = value;
	}

	public boolean isNotSaved() {
		return this.boNotSaved;
	}

	public void setConcreteClassEmbedded(Class<?> embeddedClass) {
		this.concreteClassEmbedded = embeddedClass;
	}

	public Class<?> getConcreteClassEmbedded() {
		return concreteClassEmbedded;
	}

	public void setConcreteClassProperty(Class<?> propertyClass) {
		this.concreteClassProperty = propertyClass;
	}

	public Class<?> getConcreteClassProperty() {
		return concreteClassProperty;
	}

	public void setBoEmbedded(boolean value) {
		this.boEmbedded = value;
	}

	public Class<?> getConcreteType() {
		if (this.getConcreteClassEmbedded() != null) {
			if (this.getConcreteClassEmbedded() != Object.class) {
				return this.getConcreteClassEmbedded();
			}
		}

		if (this.getConcreteClassProperty() != null) {
			if (this.getConcreteClassProperty() != Object.class) {
				return this.getConcreteClassProperty();
			}
		}
		return field.getType();
	}

	public void setBoProperty(boolean value) {
		this.boProperty = value;
	}

	public boolean isProperty() {
		return boProperty;
	}

	public Class getRealType() {
		return realType;
	}

	public Type getSubType() {
		return subType;
	}

	public Type getMapKeyType() {
		return mapKeyType;
	}

	public Type getGenericType() {
		return genericType;
	}

	public void setMapKeyType(Type mapKeyType) {
		this.mapKeyType = mapKeyType;
	}

	public void setSubType(Type subType) {
		this.subType = subType;
	}

	public boolean isBoDatabaseType() {
		return boDatabaseType;
	}

	public void setBoDatabaseType(boolean boDatabaseType) {
		this.boDatabaseType = boDatabaseType;
	}

	public String[] getConstructorArgs() {
		return constructorArgs;
	}

	public void setConstructorArgs(String[] constructorArgs) {
		this.constructorArgs = constructorArgs;
	}

	public Constructor getConstructor() {
		return constructor;
	}

	public boolean hasConstructorArgs() {
		return constructorArgs == null || constructorArgs.length == 0;
	}
	
	public void setDescriptionMappedBy(NoSQLDescriptionMappedBy mapped) {
		this.mappedBy = mapped;
	}

	public NoSQLDescriptionMappedBy getDescriptionMappedBy() {
		return mappedBy;
	}

	public String getMappedBy() {
		if (mappedBy != null)
			return this.mappedBy.getMappedBy();

		return null;
	}
	
	public boolean isMappedBy() {
		return this.mappedBy != null;
	}

	@Override
	public String toString() {
		return "\n	NoSQLDescriptionField [field=" + field + ", name=" + name + ", fieldType=" + fieldType
				+ ", temporalType=" + temporalType + ", boReference=" + boReference + ", boLob=" + boLob
				+ ", boEmbedded=" + boEmbedded + ", boIdentifier=" + boIdentifier + ", boEnumerated=" + boEnumerated
				+ ", boVersioned=" + boVersioned + ", boRequired=" + boRequired + ", collectionName=" + collectionName
				+ ", realType=" + realType + ", subType=" + subType + ", mapKeyType=" + mapKeyType + ", boSerialized="
				+ boSerialized + ", idOnlyReference=" + idOnlyReference + ", boLazyLoadReference=" + boLazyLoadReference
				+ ", boIgnoreMissingReference=" + boIgnoreMissingReference + ", boNotSaved=" + boNotSaved
				+ ", boDatabaseType=" + boDatabaseType + ", concreteClassEmbedded=" + concreteClassEmbedded
				+ ", concreteClassProperty=" + concreteClassProperty + ", boProperty=" + boProperty + ", genericType="
				+ genericType + "]";
	}

	public void setBoReference(boolean boReference) {
		this.boReference = boReference;
	}

	public void setTargetEntity(NoSQLDescriptionEntity targetEntity) {
		this.targetEntity = targetEntity;		
	}
	
	public NoSQLDescriptionEntity getTargetEntity() {
		return this.targetEntity;
	}

	public boolean hasMappedBy() {
		return isMappedBy();
	}

	public boolean isNotArrayOrCollection() {
		return !isAnyArrayOrCollectionOrMap();
	}


}
