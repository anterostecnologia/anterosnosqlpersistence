package br.com.anteros.nosql.persistence.metadata.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.anteros.core.utils.Assert;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.nosql.persistence.metadata.annotations.Cascade;
import br.com.anteros.nosql.persistence.metadata.annotations.ConstructorArgs;
import br.com.anteros.nosql.persistence.metadata.annotations.Embedded;
import br.com.anteros.nosql.persistence.metadata.annotations.Id;
import br.com.anteros.nosql.persistence.metadata.annotations.Index;
import br.com.anteros.nosql.persistence.metadata.annotations.IndexField;
import br.com.anteros.nosql.persistence.metadata.annotations.Indexes;
import br.com.anteros.nosql.persistence.metadata.annotations.Lob;
import br.com.anteros.nosql.persistence.metadata.annotations.NotSaved;
import br.com.anteros.nosql.persistence.metadata.annotations.Property;
import br.com.anteros.nosql.persistence.metadata.annotations.Reference;
import br.com.anteros.nosql.persistence.metadata.annotations.Serialized;
import br.com.anteros.nosql.persistence.metadata.annotations.Temporal;
import br.com.anteros.nosql.persistence.metadata.annotations.Transient;
import br.com.anteros.nosql.persistence.metadata.annotations.Version;
import br.com.anteros.nosql.persistence.metadata.annotations.type.CascadeType;
import br.com.anteros.nosql.persistence.metadata.annotations.type.TemporalType;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;

public class FieldConfiguration {

	private String name;
	private Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();
	private TemporalType temporalType;
	private CascadeType[] cascadeTypes;
	private Class<?> type;
	private EntityConfiguration entity;
	private Field field;
	private boolean version = false;
	private IndexConfiguration[] indexes;
	private Class<?> concreteClassEmbedded;
	private boolean notSaved;
	private boolean idOnlyReference = false;
	private boolean ignoreMissingReference = false;
	private boolean lazyLoadReference = false;
	private boolean disableCompression;
	private String defaultValue = "";
	private String[] constructorArgs = {};
	private String mappedBy = "";

	public FieldConfiguration(EntityConfiguration entity, String fieldName) {
		this.entity = entity;
		this.name = fieldName;
		this.field = ReflectionUtils.getFieldByName(entity.getSourceClazz(), name);
		if (this.field != null)
			this.type = this.field.getType();
	}

	public FieldConfiguration(EntityConfiguration entity, Field field) {
		Assert.notNull(field, "Parâmetro field é obrigatório. Erro criando FieldConfiguration.");
		this.entity = entity;
		if (field != null)
			this.name = field.getName();
		this.field = field;
		if (this.field != null)
			this.type = this.field.getType();
	}

	public String getName() {
		return name;
	}

	public FieldConfiguration name(String name) {
		if (!AbstractNoSQLObjectMapper.IGNORED_FIELDNAME.equals(name) && StringUtils.isNotBlank(name)) {
			this.name = name;
		} else {
			this.name = field.getName();
		}
		this.annotations.add(Property.class);
		return this;
	}

	public FieldConfiguration id() {
		annotations.add(Id.class);
		this.name = AbstractNoSQLObjectMapper.ID_KEY;
		return this;
	}

	public FieldConfiguration lob(String value) {
		annotations.add(Lob.class);
		if (!AbstractNoSQLObjectMapper.IGNORED_FIELDNAME.equals(value) && StringUtils.isNotBlank(value)) {
			this.name = value;
		} else {
			this.name = field.getName();
		}
		return this;
	}

	public FieldConfiguration temporal(TemporalType type, String value) {
		annotations.add(Temporal.class);
		if (!AbstractNoSQLObjectMapper.IGNORED_FIELDNAME.equals(value) && StringUtils.isNotBlank(value)) {
			this.name = value;
		} else {
			this.name = field.getName();
		}
		return this;
	}

	public FieldConfiguration transientField() {
		annotations.add(Transient.class);
		return this;
	}

	public FieldConfiguration cascade(CascadeType... values) {
		annotations.add(Cascade.class);
		Set<CascadeType> cascades = new HashSet<CascadeType>();
		if (cascadeTypes != null)
			cascades.addAll(Arrays.asList(cascadeTypes));
		for (CascadeType c : values)
			cascades.add(c);

		this.cascadeTypes = cascades.toArray(new CascadeType[] {});
		return this;
	}

	public boolean isAnnotationPresent(Class annotationClass) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldConfiguration other = (FieldConfiguration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Set<Class<? extends Annotation>> getAnnotations() {
		return annotations;
	}

	public TemporalType getTemporalType() {
		return temporalType;
	}

	public CascadeType[] getCascadeTypes() {
		return cascadeTypes;
	}

	public Class<?> getType() {
		return type;
	}

	public Field getField() {
		return field;
	}

	public EntityConfiguration getEntity() {
		return entity;
	}

	@Override
	public String toString() {
		return field.getName();
	}

	public void loadAnnotations() {
		Annotation[] annotations = field.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Cascade) {
				cascade(((Cascade) annotation).values());
			} else if ((annotation instanceof Indexes) || (annotation instanceof Index)) {
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
			} else if (annotation instanceof Id) {
				id();
			} else if (annotation instanceof Lob) {
				lob(((Lob) annotation).value());
			} else if (annotation instanceof Embedded) {
				embedded(((Embedded) annotation).concreteClass(), ((Embedded) annotation).value());
			} else if (annotation instanceof Temporal) {
				temporal(((Temporal) annotation).type(), ((Temporal) annotation).value());
			} else if (annotation instanceof Transient) {
				transientField();
			} else if (annotation instanceof NotSaved) {
				notSavedField();
			} else if (annotation instanceof Property) {
				name(((Property) annotation).value()).defaultValue(((Property) annotation).defaultValue());
			} else if (annotation instanceof Reference) {
				Reference ref = (Reference) annotation;
				reference(ref.value(), ref.ignoreMissing(), ref.lazy(), ref.idOnly(), ref.mappedBy());
			} else if (annotation instanceof Serialized) {
				Serialized ser = (Serialized) annotation;
				serialized(ser.value(), ser.disableCompression());
			} else if (annotation instanceof Version) {
				version(((Version) annotation).value());
			} else if (annotation instanceof ConstructorArgs) {
				constructorArgs(((ConstructorArgs) annotation).value());
			}
		}
	}

	public FieldConfiguration serialized(String value, boolean disableCompression) {
		this.disableCompression = disableCompression;
		if (!AbstractNoSQLObjectMapper.IGNORED_FIELDNAME.equals(value)) {
			this.name = value;
		}
		return this;
	}

	public FieldConfiguration notSavedField() {
		this.notSaved = true;
		this.annotations.add(NotSaved.class);
		return this;
	}

	public FieldConfiguration embedded(Class<?> concreteClass, String value) {
		this.concreteClassEmbedded = concreteClass;
		if (!AbstractNoSQLObjectMapper.IGNORED_FIELDNAME.equals(value) && StringUtils.isNotBlank(value)) {
			this.name = value;
		} else {
			this.name = field.getName();
		}
		this.annotations.add(Embedded.class);
		return this;
	}

	public boolean isVersion() {
		return version;
	}

	public FieldConfiguration version(String value) {
		this.annotations.add(Version.class);
		this.version = true;
		if (!AbstractNoSQLObjectMapper.IGNORED_FIELDNAME.equals(value)) {
			this.name = value;
		}
		return this;
	}

	public IndexConfiguration[] getIndexes() {
		return indexes;
	}

	public void indexes(IndexConfiguration[] indexes) {
		this.indexes = indexes;
	}

	public FieldConfiguration index(IndexConfiguration[] indexes) {
		this.annotations.add(Index.class);
		this.indexes = indexes;
		return this;
	}

	public FieldConfiguration index(IndexConfiguration index) {
		this.annotations.add(Index.class);
		this.indexes = new IndexConfiguration[] { index };
		return this;
	}

	public boolean isSimpleField() {
		return ReflectionUtils.isSimpleField(field);

	}

	public boolean isId() {
		return isAnnotationPresent(Id.class);
	}

	public EntityConfiguration getEntityConfigurationBySourceClass(Class<?> sourceClazz) {
		return entity.getEntityConfigurationBySourceClass(sourceClazz);
	}

	public boolean isMap() {
		return ReflectionUtils.isImplementsInterface(this.getField().getType(), Map.class);
	}

	public boolean isCollection() {
		return ReflectionUtils.isImplementsInterface(this.getField().getType(), Collection.class);
	}

	public boolean isTransient() {
		return this.isAnnotationPresent(new Class[] { Transient.class });
	}

	public boolean isCollectionOrMap() {
		return isCollection() || isMap();
	}

	public Class<?> getConcreteClassEmbedded() {
		return concreteClassEmbedded;
	}

	public boolean isNotSaved() {
		return notSaved;
	}

	public boolean isIdOnlyReference() {
		return idOnlyReference;
	}

	public FieldConfiguration idOnlyReference(boolean idOnlyReference) {
		this.idOnlyReference = idOnlyReference;
		return this;
	}

	public boolean isIgnoreMissingReference() {
		return ignoreMissingReference;
	}

	public FieldConfiguration ignoreMissingReference(boolean ignoreMissingReference) {
		this.ignoreMissingReference = ignoreMissingReference;
		return this;
	}

	public boolean isLazyLoadReference() {
		return lazyLoadReference;
	}

	public FieldConfiguration lazyLoadReference(boolean lazyLoadReference) {
		this.lazyLoadReference = lazyLoadReference;
		return this;
	}

	public FieldConfiguration reference(String value, boolean ignoreMissingReference, boolean lazyLoadReference,
			boolean idOnlyReference, String mappedBy) {
		this.ignoreMissingReference = ignoreMissingReference;
		this.lazyLoadReference = lazyLoadReference;
		this.idOnlyReference = idOnlyReference;
		this.mappedBy = mappedBy;
		this.annotations.add(Reference.class);
		if (!AbstractNoSQLObjectMapper.IGNORED_FIELDNAME.equals(value) && StringUtils.isNotBlank(value)) {
			this.name = value;
		} else {
			this.name = field.getName();
		}
		return this;
	}

	public boolean isDisableCompression() {
		return disableCompression;
	}

	public FieldConfiguration disableCompression(boolean disableCompression) {
		this.disableCompression = disableCompression;
		return this;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public FieldConfiguration defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public String[] getConstructorArgs() {
		return constructorArgs;
	}

	public FieldConfiguration constructorArgs(String[] constructorArgs) {
		this.constructorArgs = constructorArgs;
		this.annotations.add(ConstructorArgs.class);
		return this;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public FieldConfiguration mappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
		return this;
	}

}
