package br.com.anteros.nosql.persistence.metadata;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.nosql.persistence.converters.NoSQLTypeConverter;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.annotations.AfterLoad;
import br.com.anteros.nosql.persistence.metadata.annotations.AfterSave;
import br.com.anteros.nosql.persistence.metadata.annotations.BeforeLoad;
import br.com.anteros.nosql.persistence.metadata.annotations.BeforeSave;
import br.com.anteros.nosql.persistence.metadata.annotations.type.ScopeType;
import br.com.anteros.nosql.persistence.metadata.configuration.ClassMethodPair;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;
import br.com.anteros.nosql.persistence.session.mapping.NoSQLEntityInterceptor;

public class NoSQLDescriptionEntity {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(NoSQLDescriptionEntity.class.getName());

	private Class<?> entityClass;
	private List<NoSQLDescriptionField> fields = new LinkedList<>();
	private List<NoSQLDescriptionIndex> indexes = new LinkedList<>();
	private List<Class<? extends NoSQLTypeConverter>> converters = new LinkedList<>();
	private String collectionName;
	private String discriminatorFieldName;
	private String discriminatorValue;
	private ScopeType scope = ScopeType.TRANSACTION;
	private int maxTimeMemory = 0;
	private boolean boAbstractClass;
	private boolean noClassnameStored = false;
	private NoSQLDialect dialect;
	private boolean boEmbedded = false;
	private boolean boNotSaved = false;
	private String concern = "";

	private Map<Class<? extends Annotation>, List<ClassMethodPair>> lifeCycleMethods = new HashMap<Class<? extends Annotation>, List<ClassMethodPair>>();

	public NoSQLDescriptionEntity(Class<?> sourceClazz, NoSQLDialect dialect) {
		this.entityClass = sourceClazz;
		this.dialect = dialect;
	}

	public boolean isVersioned() {
		return false;
	}

	public List<NoSQLDescriptionField> getDescriptionFields() {
		return fields;
	}

	public Set<String> getAllFieldNames() {
		return null;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public List<NoSQLDescriptionIndex> getDescriptionIndexes() {
		return indexes;
	}

	public void setDescritionIndexes(List<NoSQLDescriptionIndex> indexes) {
		this.indexes = indexes;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getDiscriminatorFieldName() {
		return discriminatorFieldName;
	}

	public void setDiscriminatorFieldName(String discriminatorFieldName) {
		this.discriminatorFieldName = discriminatorFieldName;
	}

	public String getDiscriminatorValue() {
		return discriminatorValue;
	}

	public void setDiscriminatorValue(String discriminatorValue) {
		this.discriminatorValue = discriminatorValue;
	}

	public ScopeType getScope() {
		return scope;
	}

	public void setScope(ScopeType scope) {
		this.scope = scope;
	}

	public int getMaxTimeMemory() {
		return maxTimeMemory;
	}

	public void setMaxTimeMemory(int maxTimeMemory) {
		this.maxTimeMemory = maxTimeMemory;
	}

	public void setDescriptionFields(List<NoSQLDescriptionField> fields) {
		this.fields = fields;
	}

	public List<NoSQLDescriptionField> getDescriptionFieldsExcludingIds() {
		List<NoSQLDescriptionField> result = new ArrayList<NoSQLDescriptionField>();
		for (NoSQLDescriptionField f : fields) {
			if (!f.isIdentifier())
				result.add(f);
		}
		return result;
	}

	public void add(NoSQLDescriptionField descriptionField) {
		this.fields.add(descriptionField);

	}

	public List<NoSQLDescriptionField> getFieldsModified(NoSQLSession session, Object object) throws Exception {
		List<NoSQLDescriptionField> result = new ArrayList<NoSQLDescriptionField>();
		NoSQLFieldEntityValue lastFieldValue;
		NoSQLFieldEntityValue newFieldValue;
		for (NoSQLDescriptionField field : fields) {
			if (fieldCanbeChanged(session, object, field.getField().getName())) {
				lastFieldValue = getLastFieldEntityValue(session, object, field.getField().getName());
				newFieldValue = field.getFieldEntityValue(session, object);
				if ((lastFieldValue != null) || (newFieldValue != null)) {
					if (((lastFieldValue == null) && (newFieldValue != null))
							|| ((lastFieldValue != null) && (newFieldValue == null))
							|| (newFieldValue.compareTo(lastFieldValue) != 0))
						result.add(field);
				}
			}
		}
		return result;
	}

	public NoSQLFieldEntityValue getOriginalFieldEntityValue(NoSQLSession session, Object object, String fieldName)
			throws Exception {
		NoSQLEntityManaged entityManaged = session.getPersistenceContext().getEntityManaged(object);
		if (entityManaged != null) {
			for (NoSQLFieldEntityValue field : entityManaged.getOriginalValues()) {
				if (field.getName().equals(fieldName))
					return field;
			}
		}
		return null;
	}

	public NoSQLFieldEntityValue getLastFieldEntityValue(NoSQLSession session, Object object, String fieldName)
			throws Exception {
		NoSQLEntityManaged entityManaged = session.getPersistenceContext().getEntityManaged(object);
		if (entityManaged != null) {
			for (NoSQLFieldEntityValue field : entityManaged.getLastValues()) {
				if (field.getName().equals(fieldName))
					return field;
			}
		}
		return null;
	}

	public boolean fieldCanbeChanged(NoSQLSession session, Object object, String fieldName) throws Exception {
		NoSQLEntityManaged entityManaged = session.getPersistenceContext().getEntityManaged(object);
		if (entityManaged != null) {
			for (String field : entityManaged.getFieldsForUpdate()) {
				if (field.equals(fieldName))
					return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getEntityClass().getName() + ":" + collectionName + "";
	}

	public List<NoSQLDescriptionField> getFields() {
		return fields;
	}

	public void setFields(List<NoSQLDescriptionField> fields) {
		this.fields = fields;
	}

	public NoSQLDescriptionField getDescriptionField(String name) {
		for (NoSQLDescriptionField f : fields) {
			if (name.equalsIgnoreCase(f.getField().getName())) {
				return f;
			}
		}
		return null;
	}

	public Object getIdentifierValue(Object value) throws Exception {
		for (NoSQLDescriptionField f : fields) {
			if (f.isIdentifier()) {
				return f.getObjectValue(value);
			}
		}
		return null;
	}

	public boolean isAbstractClass() {
		return boAbstractClass;
	}

	public void setBoAbstractClass(boolean boAbstractClass) {
		this.boAbstractClass = boAbstractClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public boolean hasDiscriminatorValue() {
		return StringUtils.isNotEmpty(discriminatorValue);
	}

	public void addAllDescriptionIndex(List<NoSQLDescriptionIndex> descriptionIndexes) {
		this.indexes.addAll(descriptionIndexes);
	}

	public boolean isNoClassnameStored() {
		return noClassnameStored;
	}

	public void setNoClassnameStored(boolean noClassnameStored) {
		this.noClassnameStored = noClassnameStored;
	}

	public Field getIdField() {
		for (NoSQLDescriptionField descriptionField : fields) {
			if (descriptionField.isIdentifier()) {
				return descriptionField.getField();
			}
		}
		return null;
	}

	public NoSQLDescriptionField getDescriptionIdField() {
		for (NoSQLDescriptionField descriptionField : fields) {
			if (descriptionField.isIdentifier()) {
				return descriptionField;
			}
		}
		return null;
	}

	public NoSQLDescriptionField getDescriptionVersionField() {
		for (NoSQLDescriptionField descriptionField : fields) {
			if (descriptionField.isVersioned()) {
				return descriptionField;
			}
		}
		return null;
	}

	public NoSQLDialect getDialect() {
		return dialect;
	}

	public List<Class<? extends NoSQLTypeConverter>> getConverters() {
		return converters;
	}

	public void setConverters(List<Class<? extends NoSQLTypeConverter>> converters) {
		this.converters = converters;
	}

	public boolean isEmbedded() {
		return boEmbedded;
	}

	public void setEmbedded(boolean boEmbedded) {
		this.boEmbedded = boEmbedded;
	}

	public boolean isEntity() {
		return !this.boEmbedded;
	}

	public String print() {
		return "NoSQLDescriptionEntity [entityClass=" + entityClass + ",\n fields=" + fields + ",\n indexes=" + indexes
				+ ",\n converters=" + converters + ",\n collectionName=" + collectionName
				+ ",\n discriminatorFieldName=" + discriminatorFieldName + ",\n discriminatorValue="
				+ discriminatorValue + ",\n scope=" + scope + ",\n maxTimeMemory=" + maxTimeMemory
				+ ",\n boAbstractClass=" + boAbstractClass + ",\n noClassnameStored=" + noClassnameStored
				+ ",\n dialect=" + dialect + ",\n boEmbedded=" + boEmbedded + "]";
	}

	public List<ClassMethodPair> getLifeCycleMethods(final Class<Annotation> clazz) {
		return lifeCycleMethods.get(clazz);
	}

	private Object getOrCreateInstance(final Class<?> clazz, final AbstractNoSQLObjectMapper mapper) {
		if (mapper.getInstanceCache().containsKey(clazz)) {
			return mapper.getInstanceCache().get(clazz);
		}

		final Object o = mapper.getOptions().getObjectFactory().createInstance(clazz);
		final Object nullO = mapper.getInstanceCache().put(clazz, o);
		if (nullO != null) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Race-condition, created duplicate class: " + clazz);
			}
		}

		return o;

	}

	public Object callLifecycleMethods(final Class<? extends Annotation> event, final Object entity, final Object dbObj,
			final AbstractNoSQLObjectMapper mapper) {
		final List<ClassMethodPair> methodPairs = getLifeCycleMethods((Class<Annotation>) event);
		Object retDbObj = dbObj;
		try {
			Object tempObj;
			if (methodPairs != null) {
				final HashMap<Class<?>, Object> toCall = new HashMap<Class<?>, Object>(
						(int) (methodPairs.size() * 1.3));
				for (final ClassMethodPair cm : methodPairs) {
					toCall.put(cm.clazz, null);
				}
				for (final Class<?> c : toCall.keySet()) {
					if (c != null) {
						toCall.put(c, getOrCreateInstance(c, mapper));
					}
				}

				for (final ClassMethodPair cm : methodPairs) {
					final Method method = cm.method;
					final Object inst = toCall.get(cm.clazz);
					method.setAccessible(true);

					if (LOG.isDebugEnabled()) {
						LOG.debug(
								format("Calling lifecycle method(@%s %s) on %s", event.getSimpleName(), method, inst));
					}

					if (inst == null) {
						if (method.getParameterTypes().length == 0) {
							tempObj = method.invoke(entity);
						} else {
							tempObj = method.invoke(entity, retDbObj);
						}
					} else if (method.getParameterTypes().length == 0) {
						tempObj = method.invoke(inst);
					} else if (method.getParameterTypes().length == 1) {
						tempObj = method.invoke(inst, entity);
					} else {
						tempObj = method.invoke(inst, entity, retDbObj);
					}

					if (tempObj != null) {
						retDbObj = tempObj;
					}
				}
			}

			callGlobalInterceptors(event, entity, dbObj, mapper);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return retDbObj;
	}

	private void callGlobalInterceptors(final Class<? extends Annotation> event, final Object entity,
			final Object dbObj, final AbstractNoSQLObjectMapper mapper) {
		for (final NoSQLEntityInterceptor ei : mapper.getInterceptors()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Calling interceptor method " + event.getSimpleName() + " on " + ei);
			}

			if (event.equals(BeforeLoad.class)) {
				ei.preLoad(entity, dbObj, mapper);
			} else if (event.equals(AfterLoad.class)) {
				ei.postLoad(entity, dbObj, mapper);
			} else if (event.equals(BeforeSave.class)) {
				ei.prePersist(entity, dbObj, mapper);
			} else if (event.equals(BeforeSave.class)) {
				ei.preSave(entity, dbObj, mapper);
			} else if (event.equals(AfterSave.class)) {
				ei.postPersist(entity, dbObj, mapper);
			}
		}
	}

	public Map<Class<? extends Annotation>, List<ClassMethodPair>> getLifeCycleMethods() {
		return lifeCycleMethods;
	}

	public void setLifeCycleMethods(Map<Class<? extends Annotation>, List<ClassMethodPair>> lifeCycleMethods) {
		this.lifeCycleMethods = lifeCycleMethods;
	}

	public boolean isNotSaved() {
		return boNotSaved;
	}

	public void setBoNotSaved(boolean boNotSaved) {
		this.boNotSaved = boNotSaved;
	}

	public boolean hasVersionField() {
		for (NoSQLDescriptionField descriptionField : fields) {
			if (descriptionField.isVersioned()) {
				return true;
			}
		}
		return false;
	}

	public String getConcern() {
		return concern;
	}

	public void setConcern(String concern) {
		this.concern = concern;
	}

	public NoSQLDescriptionField getDescriptionFieldByFieldName(String fieldName) {
		for (NoSQLDescriptionField descriptionField : fields) {
			if (descriptionField.getField().getName().equals(fieldName)) {
				return descriptionField;
			}
		}
		return null;
	}

	public Object getVersionValue(Object source) {
		NoSQLDescriptionField descriptionVersion = getDescriptionVersionField();
		if (descriptionVersion == null) {
			return null;
		}

		return descriptionVersion.getObjectValue(source);

	}

	public boolean hasVersionProperty() {
		return getDescriptionVersionField() != null;
	}

	public boolean hasDescriptionFieldWithMappedBy(Class<?> sourceType, String mappedBy) {
		NoSQLDescriptionField result = getDescriptionFieldWithMappedBy(sourceType, mappedBy);
		return (result != null);
	}

	public NoSQLDescriptionField getDescriptionFieldWithMappedBy(Class<?> sourceType, String mappedBy) {
		for (NoSQLDescriptionField descriptionField : getDescriptionFields()) {
			if (mappedBy.equals(descriptionField.getMappedBy())) {
				if (descriptionField.getTargetClass().equals(sourceType))
					return descriptionField;
			}
		}
		return null;
	}

}
