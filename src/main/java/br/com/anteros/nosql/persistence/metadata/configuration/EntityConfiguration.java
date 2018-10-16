package br.com.anteros.nosql.persistence.metadata.configuration;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.nosql.persistence.converters.NoSQLTypeConverter;
import br.com.anteros.nosql.persistence.metadata.annotations.AfterLoad;
import br.com.anteros.nosql.persistence.metadata.annotations.AfterSave;
import br.com.anteros.nosql.persistence.metadata.annotations.BeforeLoad;
import br.com.anteros.nosql.persistence.metadata.annotations.BeforeSave;
import br.com.anteros.nosql.persistence.metadata.annotations.Converters;
import br.com.anteros.nosql.persistence.metadata.annotations.DiscriminatorField;
import br.com.anteros.nosql.persistence.metadata.annotations.DiscriminatorValue;
import br.com.anteros.nosql.persistence.metadata.annotations.Embedded;
import br.com.anteros.nosql.persistence.metadata.annotations.Entity;
import br.com.anteros.nosql.persistence.metadata.annotations.EntityListeners;
import br.com.anteros.nosql.persistence.metadata.annotations.EnumValue;
import br.com.anteros.nosql.persistence.metadata.annotations.EnumValues;
import br.com.anteros.nosql.persistence.metadata.annotations.Index;
import br.com.anteros.nosql.persistence.metadata.annotations.IndexField;
import br.com.anteros.nosql.persistence.metadata.annotations.Indexes;
import br.com.anteros.nosql.persistence.metadata.annotations.NotSaved;
import br.com.anteros.nosql.persistence.metadata.annotations.ReadOnly;
import br.com.anteros.nosql.persistence.metadata.annotations.type.ScopeType;


public class EntityConfiguration {

	private static final List<Class<? extends Annotation>> LIFECYCLE_ANNOTATIONS = asList(BeforeSave.class,
			BeforeSave.class, BeforeLoad.class, AfterSave.class, AfterLoad.class);

	private Class<? extends Serializable> sourceClazz;
	private List<FieldConfiguration> fields = new LinkedList<FieldConfiguration>();
	private List<Class<? extends NoSQLTypeConverter>> converters = new LinkedList<>();
	private IndexConfiguration[] indexes = {};
	private String collectionName;
	private Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();
	private String discriminatorFieldName;
	private String discriminatorValue;
	private ScopeType scope = ScopeType.TRANSACTION;
	private EnumValueConfiguration[] enumValues = {};
	private NoSQLPersistenceModelConfiguration model;
	private boolean noClassnameStored = false;
	private Class<?> concreteclass;
	private boolean boNotSaved;
	private String concern;
	private Map<Class<? extends Annotation>, List<ClassMethodPair>> lifeCycleMethods = new HashMap<Class<? extends Annotation>, List<ClassMethodPair>>();

	public EntityConfiguration(Class<? extends Serializable> sourceClazz, NoSQLPersistenceModelConfiguration model) {
		this.sourceClazz = sourceClazz;
		this.model = model;
		annotations.add(Entity.class);
	}

	public Class<? extends Serializable> getSourceClazz() {
		return sourceClazz;
	}

	public FieldConfiguration addField(String fieldName) throws Exception {
		for (FieldConfiguration field : fields) {
			if (field.getName().equals(fieldName))
				throw new NoSQLConfigurationException(
						"Campo " + fieldName + " já adicionado na Entidade " + sourceClazz.getName());
		}

		if (ReflectionUtils.getFieldByName(sourceClazz, fieldName) == null)
			throw new NoSQLConfigurationException(
					"Campo " + fieldName + " não encontrado na Classe " + sourceClazz.getName());

		FieldConfiguration field = new FieldConfiguration(this, fieldName);
		fields.add(field);
		return field;
	}

	public EntityConfiguration collectionName(String collectionName) {
		this.collectionName = collectionName;
		annotations.add(Entity.class);
		return this;
	}

	public EntityConfiguration table(String collectionName, IndexConfiguration[] indexes) {
		this.collectionName = collectionName;
		this.indexes = indexes;
		annotations.add(Entity.class);
		return this;
	}

	public EntityConfiguration readOnly() {
		annotations.add(ReadOnly.class);
		return this;
	}

	public EntityConfiguration discriminatorField(String name) {
		annotations.add(DiscriminatorField.class);
		this.discriminatorFieldName = name;
		return this;
	}

	public EntityConfiguration discriminatorValue(String value) {
		annotations.add(DiscriminatorValue.class);
		this.discriminatorValue = value;
		return this;
	}

	public EntityConfiguration enumValues(EnumValueConfiguration[] value) {
		annotations.add(EnumValues.class);
		this.enumValues = value;
		return this;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public IndexConfiguration[] getIndexes() {
		return indexes;
	}

	public List<FieldConfiguration> getFields() {
		return fields;
	}

	public Set<Class<? extends Annotation>> getAnnotations() {
		return annotations;
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return annotations.contains(annotationClass);
	}

	public boolean isAnnotationPresent(Class[] annotationClasses) {
		for (Class c : annotationClasses) {
			if (isAnnotationPresent(c)) {
				return true;
			}
		}
		return false;
	}

	public String getDiscriminatorField() {
		return discriminatorFieldName;
	}

	public String getDiscriminatorValue() {
		return discriminatorValue;
	}

	public EnumValueConfiguration[] getEnumValues() {
		return enumValues;
	}

	public FieldConfiguration[] getAllFields() {
		Set<FieldConfiguration> allFields = new LinkedHashSet<FieldConfiguration>();
		allFields.addAll(getFields());
		Class<?> clazz = sourceClazz;
		while ((clazz != null) && (clazz != Object.class)) {
			EntityConfiguration entityConfiguration = model.getEntities().get(clazz);
			if (entityConfiguration == null)
				break;
			allFields.addAll(entityConfiguration.getFields());
			clazz = clazz.getSuperclass();
		}

		return allFields.toArray(new FieldConfiguration[] {});
	}

	public NoSQLPersistenceModelConfiguration getModel() {
		return model;
	}

	public int countNumberOfAnnotation(Class<? extends Annotation> annotationClass) {
		int result = 0;
		for (FieldConfiguration field : getFields()) {
			if (field.isAnnotationPresent(annotationClass))
				result++;
		}
		return result;
	}

	public boolean isEnum() {
		return sourceClazz.isEnum();
	}

	@Override
	public String toString() {
		return sourceClazz.getName();
	}

	public void loadAnnotations() {
		Annotation[] annotations = sourceClazz.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Entity) {
				this.annotations.add(Entity.class);
				this.collectionName(((Entity) annotation).value());
				this.noClassnameStored(((Entity) annotation).noClassnameStored());
				this.concern(((Entity) annotation).concern());
			} else if (annotation instanceof Embedded) {
				this.annotations.add(Embedded.class);
				this.concreteclass(((Embedded) annotation).concreteClass());
			} else if ((annotation instanceof Indexes) || (annotation instanceof Index)
					|| (annotation instanceof Index.List)) {
				Index[] indexes = null;
				if (annotation instanceof Indexes)
					indexes = ((Indexes) annotation).value();
				else if (annotation instanceof Index)
					indexes = new Index[] { (Index) annotation };
				else if (annotation instanceof Index.List)
					indexes = ((Index.List) annotation).value();

				IndexConfiguration[] indexesConf = null;
				if (indexes != null) {
					indexesConf = new IndexConfiguration[indexes.length];
					for (int i = 0; i < indexes.length; i++) {
						indexesConf[i] = new IndexConfiguration(indexes[i].options().name());
						for (int j = 0; j < indexes[i].fields().length; j++) {
							IndexField idxf = indexes[i].fields()[j];
							indexesConf[i].addField(idxf.value(), idxf.type(), idxf.weight());
						}
					}
				}
				if ((annotation instanceof Indexes) || (annotation instanceof Index.List))
					indexes(indexesConf);
				else
					index(indexesConf);

			} else if (annotation instanceof DiscriminatorField) {
				discriminatorField(((DiscriminatorField) annotation).name());
			} else if (annotation instanceof DiscriminatorValue) {
				discriminatorValue(((DiscriminatorValue) annotation).value());
			} else if ((annotation instanceof EnumValues) || (annotation instanceof EnumValue.List)) {
				EnumValue[] values = null;
				if (annotation instanceof EnumValues) {
					values = ((EnumValues) annotation).value();
				} else {
					values = ((EnumValue.List) annotation).value();
				}
				if (values != null) {
					EnumValueConfiguration[] enValues = new EnumValueConfiguration[values.length];
					for (int i = 0; i < values.length; i++)
						enValues[i] = new EnumValueConfiguration(values[i].enumValue(), values[i].value());
					enumValues(enValues);
				}
			} else if (annotation instanceof ReadOnly) {
				readOnly();
			} else if (annotation instanceof Converters) {
				converters(((Converters) annotation).value());
			} else if (annotation instanceof EntityListeners) {
				entityListeners(((EntityListeners) annotation).value());
			} else if (annotation instanceof NotSaved) {
				notSaved(true);	
			}
		}

		Field[] fields = ReflectionUtils.getAllDeclaredFields(sourceClazz);
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				FieldConfiguration fieldConfiguration = new FieldConfiguration(this, field);
				fieldConfiguration.loadAnnotations();
				this.fields.add(fieldConfiguration);
			}
		}
	}

	public EntityConfiguration entityListeners(Class<?>[] listeners) {
		this.annotations.add(EntityListeners.class);
		for (Class<?> listener : listeners) {
			this.entityListener(listener);
		}

		return this;
	}

	public EntityConfiguration addLifecycleEventMethod(LifeCycleType lifeCycleType, final String m,
			final Class<?> clazz) {
		Class<? extends Annotation> lceClazz = AfterLoad.class;
		if (lifeCycleType.equals(LifeCycleType.POST_PERSIST)) {
			lceClazz = AfterSave.class;
		} else if (lifeCycleType.equals(LifeCycleType.PRE_LOAD)) {
			lceClazz = BeforeLoad.class;
		} else if (lifeCycleType.equals(LifeCycleType.PRE_SAVE)) {
			lceClazz = BeforeSave.class;
		}
		Method method = ReflectionUtils.getMethodByName(clazz, m);
		if (method == null) {
			throw new MetadataConfigurationException("Method " + m + " not found on class " + clazz.getName());
		}
		addLifeCycleEventMethod(lceClazz, method, clazz.equals(sourceClazz) ? null : clazz);

		return this;
	}

	protected void addLifeCycleEventMethod(final Class<? extends Annotation> lceClazz, final Method m,
			final Class<?> clazz) {
		final ClassMethodPair cm = new ClassMethodPair(clazz, m);
        if (lifeCycleMethods.containsKey(lceClazz)) {
            lifeCycleMethods.get(lceClazz).add(cm);
        } else {
            final List<ClassMethodPair> methods = new ArrayList<ClassMethodPair>();
            methods.add(cm);
            lifeCycleMethods.put(lceClazz, methods);
        }
	}

	public EntityConfiguration entityListener(Class<?> listener) {
		this.annotations.add(EntityListeners.class);
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(listener);
		for (Method m : methods) {
			for (final Class<? extends Annotation> c : LIFECYCLE_ANNOTATIONS) {
				if (m.isAnnotationPresent(c)) {
					addLifeCycleEventMethod(c, m, listener.equals(sourceClazz) ? null : listener);
				}
			}
		}
		return this;
	}

	public Map<Class<? extends Annotation>, List<ClassMethodPair>> getLifeCycleMethods() {
		return lifeCycleMethods;
	}

	private EntityConfiguration concreteclass(Class<?> concreteClass) {
		this.concreteclass = concreteClass;
		return this;
	}

	public ScopeType getScope() {
		return scope;
	}

	public EntityConfiguration scope(ScopeType scope) {
		this.scope = scope;
		return this;
	}

	public EntityConfiguration indexes(IndexConfiguration[] indexes) {
		this.annotations.add(Indexes.class);
		this.indexes = indexes;
		return this;
	}

	public EntityConfiguration index(IndexConfiguration[] indexes) {
		this.annotations.add(Index.class);
		this.indexes = indexes;
		return this;
	}

	public EntityConfiguration index(IndexConfiguration index) {
		this.annotations.add(Index.class);
		this.indexes = new IndexConfiguration[] { index };
		return this;
	}

	public EntityConfiguration getEntityConfigurationBySourceClass(Class<?> sourceClazz) {
		return model.getEntityConfigurationBySourceClass(sourceClazz);
	}

	public boolean isNoClassnameStored() {
		return noClassnameStored;
	}

	public EntityConfiguration noClassnameStored(boolean noClassnameStored) {
		this.noClassnameStored = noClassnameStored;
		return this;
	}

	public List<Class<? extends NoSQLTypeConverter>> getConverters() {
		return converters;
	}

	public EntityConfiguration converters(Class<? extends NoSQLTypeConverter>[] converters) {
		this.annotations.add(Converters.class);
		this.converters = Arrays.asList(converters);
		return this;
	}

	public boolean isNotSaved() {
		return boNotSaved;
	}

	public EntityConfiguration notSaved(boolean boNotSaved) {
		this.boNotSaved = boNotSaved;
		this.annotations.add(NotSaved.class);
		return this;
	}

	public String getConcern() {
		return concern;
	}

	public EntityConfiguration concern(String concern) {
		this.concern = concern;
		return this;
	}

}
