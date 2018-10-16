package br.com.anteros.nosql.persistence.metadata;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.metadata.accessor.PropertyAccessorFactory;
import br.com.anteros.nosql.persistence.metadata.annotations.Cascade;
import br.com.anteros.nosql.persistence.metadata.annotations.ConstructorArgs;
import br.com.anteros.nosql.persistence.metadata.annotations.Converters;
import br.com.anteros.nosql.persistence.metadata.annotations.DiscriminatorField;
import br.com.anteros.nosql.persistence.metadata.annotations.DiscriminatorValue;
import br.com.anteros.nosql.persistence.metadata.annotations.Embedded;
import br.com.anteros.nosql.persistence.metadata.annotations.Entity;
import br.com.anteros.nosql.persistence.metadata.annotations.EnumValues;
import br.com.anteros.nosql.persistence.metadata.annotations.Enumerated;
import br.com.anteros.nosql.persistence.metadata.annotations.Id;
import br.com.anteros.nosql.persistence.metadata.annotations.Index;
import br.com.anteros.nosql.persistence.metadata.annotations.Indexes;
import br.com.anteros.nosql.persistence.metadata.annotations.Lob;
import br.com.anteros.nosql.persistence.metadata.annotations.NotSaved;
import br.com.anteros.nosql.persistence.metadata.annotations.Property;
import br.com.anteros.nosql.persistence.metadata.annotations.Reference;
import br.com.anteros.nosql.persistence.metadata.annotations.Serialized;
import br.com.anteros.nosql.persistence.metadata.annotations.Temporal;
import br.com.anteros.nosql.persistence.metadata.annotations.Transient;
import br.com.anteros.nosql.persistence.metadata.annotations.Version;
import br.com.anteros.nosql.persistence.metadata.comparator.DependencyComparator;
import br.com.anteros.nosql.persistence.metadata.configuration.EntityConfiguration;
import br.com.anteros.nosql.persistence.metadata.configuration.EnumValueConfiguration;
import br.com.anteros.nosql.persistence.metadata.configuration.FieldConfiguration;
import br.com.anteros.nosql.persistence.metadata.configuration.IndexConfiguration;
import br.com.anteros.nosql.persistence.metadata.configuration.NoSQLPersistenceModelConfiguration;
import br.com.anteros.nosql.persistence.metadata.exception.NoSQLDescriptionEntityManagerException;

public class NoSQLDescriptionEntityManager {

	private Map<Class<? extends Serializable>, NoSQLDescriptionEntity> entities = new LinkedHashMap<Class<? extends Serializable>, NoSQLDescriptionEntity>();
	private boolean loaded = false;
	private boolean validate = true;
	private PropertyAccessorFactory propertyAccessorFactory;
	private NoSQLDialect dialect;

	private Set<NoSQLDescriptionEntity> processedEntities = new HashSet<NoSQLDescriptionEntity>();

	/**
	 * Método utilizado para ler as configurações das Entidades.
	 * 
	 * param clazzes throws Exception
	 */
	public void load(List<Class<? extends Serializable>> clazzes, boolean validate,
			PropertyAccessorFactory propertyAccessorFactory, NoSQLDialect dialect) throws Exception {
		this.propertyAccessorFactory = propertyAccessorFactory;
		if (!isLoaded()) {
			Collections.sort(clazzes, new DependencyComparator());
			NoSQLPersistenceModelConfiguration modelConfiguration = new NoSQLPersistenceModelConfiguration();
			for (Class<? extends Serializable> sourceClazz : clazzes) {
				modelConfiguration.loadAnnotationsByClass(sourceClazz);
			}
			modelConfiguration.createOmmitedOrDefaultSettings();
			this.validate = validate;
			load(modelConfiguration, propertyAccessorFactory, dialect);
		}
	}

	/**
	 * Método utilizado para ler as configurações das classes configuradas no
	 * modelo. param modelConfiguration throws Exception
	 */
	public void load(NoSQLPersistenceModelConfiguration modelConfiguration,
			PropertyAccessorFactory propertyAccessorFactory, NoSQLDialect dialect) throws Exception {
		this.propertyAccessorFactory = propertyAccessorFactory;
		this.dialect = dialect;
		if (!isLoaded()) {

			modelConfiguration.sortByDependency();

			for (Class<? extends Serializable> sourceClazz : modelConfiguration.getEntities().keySet()) {
				if (!sourceClazz.isEnum()) { // Se não é um Enum é uma Entidade
					addEntityClass(sourceClazz,
							loadBasicConfigurations(sourceClazz, modelConfiguration.getEntities().get(sourceClazz)));
				}
			}

			processedEntities.clear();

			for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
				if (!processedEntities.contains(descriptionEntity)) {
					loadConfigurationsSuperClass(descriptionEntity, modelConfiguration);
				}
			}

			for (NoSQLDescriptionEntity descriptionEntity : entities.values())
				loadRemainderConfigurations(descriptionEntity);

			this.loaded = true;
		}
	}

	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Executa leitura e validação das configurações básicas.
	 * 
	 * 
	 * param sourceClazz return throws Exception
	 */
	private NoSQLDescriptionEntity loadBasicConfigurations(Class<? extends Serializable> sourceClazz,
			EntityConfiguration entityConfiguration) throws Exception {
		/**
		 * Valida as configurações básicas
		 */
		validateBasicConfiguration(sourceClazz, entityConfiguration);

		/**
		 * Cria NoSQLDescriptionEntity (metadata) que irá representar(descrever) a
		 * classe
		 */
		NoSQLDescriptionEntity descriptionEntity = new NoSQLDescriptionEntity(sourceClazz, dialect);
		String collectionName;

		descriptionEntity.setBoAbstractClass(ReflectionUtils.isAbstractClass(sourceClazz));
		descriptionEntity.setLifeCycleMethods(entityConfiguration.getLifeCycleMethods());
		descriptionEntity.setBoNotSaved(entityConfiguration.isNotSaved());
		descriptionEntity.setConcern(entityConfiguration.getConcern());

		if ((entityConfiguration.isAnnotationPresent(DiscriminatorField.class))
				&& !ReflectionUtils.isAbstractClass(sourceClazz)) {
			throw new NoSQLDescriptionFieldException("A classe " + sourceClazz
					+ " possui a configuração DiscriminatorField porém ela não é uma classe abstrata. Defina a classe como abstract.");
		}

		collectionName = sourceClazz.getSimpleName().toLowerCase();
		if (entityConfiguration.isAnnotationPresent(Entity.class)) {
			collectionName = entityConfiguration.getCollectionName();
			descriptionEntity.setNoClassnameStored(entityConfiguration.isNoClassnameStored());
		}

		descriptionEntity.setCollectionName(collectionName);
		if (entityConfiguration.isAnnotationPresent(DiscriminatorField.class)) {
			descriptionEntity.setDiscriminatorFieldName(entityConfiguration.getDiscriminatorField());
		}

		/*
		 * Adiciona os índices
		 */
		if (entityConfiguration.isAnnotationPresent(Indexes.class)
				|| entityConfiguration.isAnnotationPresent(Index.class)) {
			IndexConfiguration[] indexes = entityConfiguration.getIndexes();
			if (indexes != null) {
				for (IndexConfiguration index : indexes) {
					descriptionEntity.getDescriptionIndexes().add(new NoSQLDescriptionIndex(descriptionEntity)
							.setName(index.getName()).setBackground(index.isBackground())
							.setDisableValidation(index.isDisableValidation())
							.setExpireAfterSeconds(index.getExpireAfterSeconds()).setLanguage(index.getLanguage())
							.setLanguageOverride(index.getLanguageOverride()).setPartialFilter(index.getPartialFilter())
							.setSparse(index.isSparse()).setUnique(index.isUnique()));
				}
			}
		}

		/*
		 * Adiciona os converters
		 */
		if (entityConfiguration.isAnnotationPresent(Converters.class)) {
			descriptionEntity.setConverters(entityConfiguration.getConverters());
		}

		for (FieldConfiguration fieldConfiguration : entityConfiguration.getFields()) {
			/*
			 * Se possuir Transient
			 */
			if ((fieldConfiguration.isAnnotationPresent(Transient.class))
					|| (fieldConfiguration.getName().toLowerCase().startsWith("$javassist_read_write_handler"))
					|| (Modifier.isStatic(fieldConfiguration.getField().getModifiers())))
				continue;

			validateBasicFieldConfiguration(sourceClazz, fieldConfiguration);

			readFieldConfiguration(fieldConfiguration, descriptionEntity, entityConfiguration.getModel());

			/*
			 * Adiciona os índices
			 */
			if (fieldConfiguration.isAnnotationPresent(Indexes.class)
					|| entityConfiguration.isAnnotationPresent(Index.class)) {
				IndexConfiguration[] indexes = fieldConfiguration.getIndexes();
				if (indexes != null) {
					NoSQLDescriptionField df = descriptionEntity
							.getDescriptionField(fieldConfiguration.getField().getName());
					if (df != null) {
						for (IndexConfiguration index : indexes)
							df.getIndexes().add(new NoSQLDescriptionIndex(descriptionEntity).setName(index.getName())
									.setBackground(index.isBackground())
									.setDisableValidation(index.isDisableValidation())
									.setExpireAfterSeconds(index.getExpireAfterSeconds())
									.setLanguage(index.getLanguage()).setLanguageOverride(index.getLanguageOverride())
									.setPartialFilter(index.getPartialFilter()).setSparse(index.isSparse())
									.setUnique(index.isUnique()));
					}
				}
			}

		}

		return descriptionEntity;
	}

	private void readFieldConfiguration(FieldConfiguration fieldConfiguration, NoSQLDescriptionEntity descriptionEntity,
			NoSQLPersistenceModelConfiguration model) throws Exception {

		NoSQLDescriptionField descriptionField = new NoSQLDescriptionField(descriptionEntity,
				fieldConfiguration.getField());
		descriptionField.setName(fieldConfiguration.getName());
		if (propertyAccessorFactory != null)
			descriptionField.setPropertyAccessor(propertyAccessorFactory
					.createAccessor(descriptionEntity.getEntityClass(), fieldConfiguration.getField()));

		descriptionField.setDefaultValue(fieldConfiguration.getDefaultValue());

		if (fieldConfiguration.isAnnotationPresent(Temporal.class)) {
			descriptionField.setTemporalType(fieldConfiguration.getTemporalType());
		}

		/*
		 * Se possuir Enumerated
		 */
		if (fieldConfiguration.isAnnotationPresent(Enumerated.class)) {
			readEnumeratedConfiguration(fieldConfiguration, descriptionEntity, model, descriptionField);
		}

		/*
		 * Se possuir Version
		 */
		if (fieldConfiguration.isAnnotationPresent(Version.class)) {
			descriptionField.setVersioned(true);
		}

		/*
		 * Se possuir Property
		 */
		if (fieldConfiguration.isAnnotationPresent(Property.class)) {
			descriptionField.setBoProperty(true);
		}

		/*
		 * Se possuir Lob
		 */
		if (fieldConfiguration.isAnnotationPresent(Lob.class)) {
			descriptionField.setBoLob(true);
		}

		/*
		 * Se possuir Serialized
		 */
		if (fieldConfiguration.isAnnotationPresent(Serialized.class)) {
			descriptionField.setBoSerialized(true);
			descriptionField.setDisableCompression(fieldConfiguration.isDisableCompression());
		}

		/*
		 * Se possuir Reference
		 */
		if (fieldConfiguration.isAnnotationPresent(Reference.class)) {
			descriptionField.setBoRequired(true);
			descriptionField.setBoReference(true);
			descriptionField.setIdOnlyReference(fieldConfiguration.isIdOnlyReference());
			descriptionField.setBoLazyLoadReference(fieldConfiguration.isLazyLoadReference());
			descriptionField.setBoIgnoreMissingReference(fieldConfiguration.isIgnoreMissingReference());
			if (StringUtils.isNotBlank(fieldConfiguration.getMappedBy())) {
				descriptionField.setDescriptionMappedBy(new NoSQLDescriptionMappedBy(fieldConfiguration.getMappedBy()));
			}
		}

		/*
		 * Se possuir Embedded
		 */
		if (fieldConfiguration.isAnnotationPresent(Embedded.class)) {
			descriptionField.setBoEmbedded(true);
			descriptionField.setConcreteClassEmbedded(fieldConfiguration.getConcreteClassEmbedded());
		}

		/*
		 * Se possuir NotSaved
		 */
		if (fieldConfiguration.isAnnotationPresent(NotSaved.class)) {
			descriptionField.setBoNotSaved(true);
		}

		/*
		 * Se possuir ConstructorArgs
		 */
		if (fieldConfiguration.isAnnotationPresent(ConstructorArgs.class)) {
			descriptionField.setBoNotSaved(true);
		}

		/*
		 * Se possuir Id ou CompositeId. define como PrimaryKey e adiciona na coleção de
		 * descriptioncolumns
		 */
		if (fieldConfiguration.isAnnotationPresent(Id.class)) {
			descriptionField.setBoIdentifier(true);
			descriptionField.setBoRequired(true);
		}

		descriptionEntity.add(descriptionField);
	}

	public void readEnumeratedConfiguration(FieldConfiguration fieldConfiguration,
			NoSQLDescriptionEntity descriptionEntity, NoSQLPersistenceModelConfiguration model,
			NoSQLDescriptionField descriptionField) throws NoSQLDescriptionEntityException {
		if (fieldConfiguration.isAnnotationPresent(Enumerated.class)) {
			Map<String, String> enumValues = new HashMap<String, String>();

			Class<?> enumClass = fieldConfiguration.getType();
			if (!ReflectionUtils.isExtendsClass(Enum.class, enumClass)) {
				throw new NoSQLDescriptionEntityException("O campo " + fieldConfiguration.getName() + " da classe "
						+ descriptionEntity.getEntityClass().getName()
						+ " configurado com Enumerated deve ser do tipo Enum.");
			}

			/*
			 * Se possuir EnumValue na Classe de Enum
			 */
			EntityConfiguration enumConfiguration = model.getEntities().get(enumClass);
			if (enumConfiguration != null) {
				if (enumConfiguration.isAnnotationPresent(EnumValues.class)) {
					EnumValueConfiguration[] enumValuesConfiguration = enumConfiguration.getEnumValues();

					/*
					 * Se quantidade de constantes da Classe de Enum difere da quantidade de
					 * EnumValue.
					 */
					if (enumValuesConfiguration.length != enumClass.getEnumConstants().length)
						throw new NoSQLDescriptionEntityException("A quantidade de valores definidos no Enum "
								+ enumClass.getName()
								+ " difere da quantidade de valores definidos na configuração EnumValues.\nEnumValues->"
								+ Arrays.toString(enumValuesConfiguration) + "\n" + enumClass.getName() + "->"
								+ Arrays.toString(enumClass.getEnumConstants()));

					for (EnumValueConfiguration value : enumValuesConfiguration)
						enumValues.put(value.getEnumValue(), value.getValue());

				} else {
					for (Object value : enumClass.getEnumConstants())
						enumValues.put(value.toString(), value.toString());
				}
			} else {
				for (Object value : enumClass.getEnumConstants())
					enumValues.put(value.toString(), value.toString());
			}

			descriptionField.setEnumValues(enumValues);
			descriptionField.setBoEnumerated(true);
		}
	}

	private void loadRemainderConfigurations(NoSQLDescriptionEntity descriptionEntity) {
		for (NoSQLDescriptionField descriptionField : descriptionEntity.getDescriptionFields()) {
			if (descriptionField.isReferenced()) {
				NoSQLDescriptionEntity refDescriptionEntity = getDescriptionEntity(descriptionField.getTargetClass());
				if (refDescriptionEntity == null) {
					throw new NoSQLDescriptionEntityException("A classe " + descriptionField.getTargetClass().getName()
							+ " não foi encontrada na lista de classes configuradas. Verifique o campo "
							+ descriptionField.getName() + " da Classe "
							+ descriptionEntity.getEntityClass().getName());
				}

				descriptionField.setTargetEntity(refDescriptionEntity);
				if (descriptionField.hasMappedBy() && descriptionField.isAnyArrayOrCollection()) {
					NoSQLDescriptionMappedBy mapped;
					try {
						mapped = descriptionField.getDescriptionMappedBy();
						mapped.setDescriptionEntity(refDescriptionEntity);
					} catch (Exception ex) {
						throw new NoSQLDescriptionEntityException(
								"Erro lendo classe " + descriptionEntity.getEntityClass().getName() + ". " + " campo "
										+ descriptionField.getName() + " " + ex.getMessage());
					}

					if (mapped.getDescriptionEntity().getDescriptionField(mapped.getMappedBy()) == null) {
						throw new NoSQLDescriptionEntityException("O mapeamento do campo " + descriptionField.getName()
								+ " da classe " + descriptionEntity.getEntityClass().getName()
								+ " está incorreto. O mapeamento configurado em mappedBy=" + mapped.getMappedBy()
								+ " não foi encontrado na classe "
								+ mapped.getDescriptionEntity().getEntityClass().getName());
					}
					descriptionField.setDescriptionMappedBy(mapped);
				}
			}

		}
	}

	private void loadConfigurationsSuperClass(NoSQLDescriptionEntity descriptionEntity,
			NoSQLPersistenceModelConfiguration modelConfiguration) throws Exception {
		Class<?> sourceClazz = descriptionEntity.getEntityClass();
		NoSQLDescriptionEntity descriptionEntitySuper;

		/*
		 * Se superclasse != de Object.class e ela não possuir Inheritance
		 */
		EntityConfiguration entityConfigurationSuper = modelConfiguration.getEntities()
				.get(sourceClazz.getSuperclass());
		if ((entityConfigurationSuper != null)
				&& (entityConfigurationSuper.isAnnotationPresent(DiscriminatorValue.class))) {
			/*
			 * Recupera annotações da superclass e inclui na subclasse.
			 */

			descriptionEntitySuper = entities.get(sourceClazz.getSuperclass());
			if (!processedEntities.contains(descriptionEntitySuper)) {
				loadConfigurationsSuperClass(descriptionEntitySuper, modelConfiguration);
			}

			List<NoSQLDescriptionField> temporaryListFields = new LinkedList<NoSQLDescriptionField>();
			temporaryListFields.addAll(descriptionEntitySuper.getDescriptionFields());
			for (NoSQLDescriptionField f : descriptionEntitySuper.getDescriptionFields()) {
				if (descriptionEntity.getDescriptionField(f.getName()) != null) {
					throw new NoSQLDescriptionFieldException("Encontrado campo " + f.getName() + " duplicado na classe "
							+ sourceClazz + ". Verifique se o mesmo já não existe na super classe.");
				}
			}
			try {
				temporaryListFields.addAll(descriptionEntity.getDescriptionFields());
				descriptionEntity.getDescriptionFields().clear();
				descriptionEntity.getDescriptionFields().addAll(temporaryListFields);

				descriptionEntity.addAllDescriptionIndex(descriptionEntitySuper.getDescriptionIndexes());
			} catch (Exception ex) {
				throw new NoSQLDescriptionFieldException("Erro lendo configuração da classe " + sourceClazz.getName()
						+ ". " + Arrays.toString(ex.getStackTrace()));
			}
		}

		/*
		 * Possui DiscriminatorValue
		 */
		EntityConfiguration entityConfiguration = modelConfiguration.getEntities().get(sourceClazz);
		if (entityConfiguration.isAnnotationPresent(DiscriminatorValue.class)) {
			descriptionEntitySuper = entities.get(sourceClazz.getSuperclass());
			if (descriptionEntitySuper == null) {
				throw new NoSQLDescriptionFieldException("A Entidade " + sourceClazz.getName()
						+ " possui a configuração DiscriminatorValue mas não herda de uma outra Entidade ou a Entidade herdada não foi localizada.");
			}
			if (!processedEntities.contains(descriptionEntitySuper)) {
				loadConfigurationsSuperClass(descriptionEntitySuper, modelConfiguration);
			}
			descriptionEntity.setCollectionName(descriptionEntitySuper.getCollectionName());
			descriptionEntity.setDiscriminatorValue(entityConfiguration.getDiscriminatorValue());
		}

		processedEntities.add(descriptionEntity);
	}

	@SuppressWarnings("unused")
	private boolean existsEntityClass(Class<?> clazz) {
		return getDescriptionEntity(clazz) != null;
	}

	protected void validateBasicFieldConfiguration(Class<? extends Serializable> sourceClazz,
			FieldConfiguration fieldConfiguration) throws NoSQLDescriptionFieldException {
		if (fieldConfiguration.getAnnotations().size() == 0)
			throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " da classe "
					+ sourceClazz.getName()
					+ " não possuí nenhuma configuração. Caso o campo não seja persistido configurar como Transient.");

		if (!ReflectionUtils.hasGetterAccessor(sourceClazz, fieldConfiguration.getField())) {
			throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " da classe "
					+ sourceClazz.getName()
					+ " não possuí um método acessor (GET) configurado. Defina os métodos acessores para todos os campos das entidades.");
		}

		if (!ReflectionUtils.hasSetterAccessor(sourceClazz, fieldConfiguration.getField())) {
			throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " da classe "
					+ sourceClazz.getName()
					+ " não possuí um método acessor (SET) configurado. Defina os métodos acessores para todos os campos das entidades.");
		}

		if (validate) {
			/*
			 * Se field Date, deve possuir a configuração Temporal
			 */
			if (fieldConfiguration.getType() == java.util.Date.class
					&& !fieldConfiguration.isAnnotationPresent(Temporal.class))
				throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " da classe "
						+ sourceClazz.getName() + " é do tipo java.util.Date, mas não possui a configuração Temporal.");

			/*
			 * Valida se é um tipo primitivo
			 */
			if (fieldConfiguration.getType().isPrimitive()) {
				throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " da classe "
						+ sourceClazz.getName()
						+ " é um tipo primitivo. Utilize somente classes Wrapper's. Ex: Long, Integer, Short, Double, etc.");
			}

			/*
			 * Se possuir Cascade
			 */
			if (fieldConfiguration.isAnnotationPresent(Cascade.class)) {
				if (!ReflectionUtils.isCollection(fieldConfiguration.getType())
						&& !fieldConfiguration.isAnnotationPresent(Reference.class)) {
					throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " do tipo "
							+ fieldConfiguration.getType().getName() + " da entidade " + sourceClazz.getSimpleName()
							+ " possui a configuração Cascade que é permitida somente para implementações de java.util.Collection (Set, List) ou com a configuração Reference.");

				}
			}

			/*
			 * Se possui Lob, deve ser do tipo Byte[], byte[], implementar
			 * java.io.Serializable, Character[], char[] ou java.lang.String
			 */
			if (fieldConfiguration.isAnnotationPresent(Lob.class)) {
				if (fieldConfiguration.getType() != byte[].class && fieldConfiguration.getType() != Byte[].class
						&& Serializable.class.isAssignableFrom(fieldConfiguration.getType())
						&& fieldConfiguration.getType() != Character[].class
						&& fieldConfiguration.getType() != char[].class
						&& fieldConfiguration.getType() != java.lang.String.class) {
					throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " da entidade "
							+ fieldConfiguration.getType().getName()
							+ " possui a configuração Lob e deve ser do tipo Byte[], byte[], implementar java.io.Serializable, Character[], char[] ou java.lang.String.");
				}

			}
		}

		/*
		 * Se possuir Version, deve ser do tipo Integer, Long, Short ou Date
		 */
		if (fieldConfiguration.isAnnotationPresent(Version.class)) {
			if ((fieldConfiguration.getType() != Integer.class) && (fieldConfiguration.getType() != Long.class)
					&& (fieldConfiguration.getType() != Short.class) && (fieldConfiguration.getType() != Date.class)) {
				throw new NoSQLDescriptionFieldException("O campo " + fieldConfiguration.getName() + " da Entidade "
						+ sourceClazz.getName()
						+ " possui Version que pode ser utilizado somente com atributos do tipo Long, Integer, Short e Date.");
			}
		}

	}

	protected void validateBasicConfiguration(Class<? extends Serializable> sourceClazz,
			EntityConfiguration entityConfiguration) throws NoSQLDescriptionFieldException {
		if (validate) {
			String[] errors = DescriptionEntityAnnotationValidation.validateEntityConfiguration(sourceClazz,
					entityConfiguration);
			if (errors.length > 0)
				throw new NoSQLDescriptionFieldException(errors[0]);
		}
	}

	private void addEntityClass(Class<? extends Serializable> clazz, NoSQLDescriptionEntity descriptionEntity) {
		entities.put(clazz, descriptionEntity);
	}

	private FieldConfiguration getIdFieldConfiguration(Class<?> clazz, NoSQLPersistenceModelConfiguration model) {
		EntityConfiguration entityConfiguration = model.getEntities().get(clazz);
		if (entityConfiguration == null)
			return null;

		FieldConfiguration field = null;
		for (FieldConfiguration fieldConfiguration : entityConfiguration.getFields()) {
			if (fieldConfiguration.isAnnotationPresent(Id.class)) {
				field = fieldConfiguration;
			}

		}
		if (clazz.getSuperclass() != Object.class) {
			field = getIdFieldConfiguration(clazz.getSuperclass(), model);
		}
		return field;
	}

	public NoSQLDescriptionEntity[] getEntitiesBySuperClass(Class<?> superClass) {
		List<NoSQLDescriptionEntity> result = new ArrayList<NoSQLDescriptionEntity>();
		for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
			if ((ReflectionUtils.isExtendsClass(superClass, descriptionEntity.getEntityClass()))
					&& (descriptionEntity.getEntityClass() != superClass))
				result.add(descriptionEntity);
		}
		return result.toArray(new NoSQLDescriptionEntity[] {});
	}

	public NoSQLDescriptionEntity getEntitySuperClass(Class<?> clazz) {
		for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
			if ((ReflectionUtils.isExtendsClass(descriptionEntity.getEntityClass(), clazz))
					&& (descriptionEntity.getEntityClass() != clazz))
				if (!descriptionEntity.hasDiscriminatorValue()) {
					return descriptionEntity;
				}
		}
		return null;
	}

	public NoSQLDescriptionEntity[] getEntitiesBySuperClass(NoSQLDescriptionEntity descriptionEntity) {
		return getEntitiesBySuperClass(descriptionEntity.getEntityClass());
	}

	public NoSQLDescriptionEntity[] getEntitiesBySuperClassIncluding(Class<?> superClass) {
		List<NoSQLDescriptionEntity> result = new ArrayList<NoSQLDescriptionEntity>();
		for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
			if ((ReflectionUtils.isExtendsClass(superClass, descriptionEntity.getEntityClass())))
				result.add(descriptionEntity);
		}
		return result.toArray(new NoSQLDescriptionEntity[] {});
	}

	public NoSQLDescriptionEntity[] getEntitiesBySuperClassIncluding(NoSQLDescriptionEntity descriptionEntity) {
		return getEntitiesBySuperClassIncluding(descriptionEntity.getEntityClass());
	}

	public NoSQLDescriptionField getDescriptionFieldByCollectionName(String collectionName) {
		for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
			for (NoSQLDescriptionField descField : descriptionEntity.getDescriptionFields()) {
				if ((descField.getCollectionName() != null)
						&& (descField.getCollectionName().equalsIgnoreCase(collectionName))) {
					return descField;
				}
			}
		}
		return null;
	}

	public NoSQLDescriptionEntity getDescriptionEntity(Class<? extends Object> clazz) {
		return this.entities.get(clazz);
	}

	public Map<Class<? extends Serializable>, NoSQLDescriptionEntity> getEntities() {
		return entities;
	}

	public void setEntities(Map<Class<? extends Serializable>, NoSQLDescriptionEntity> entities) {
		this.entities = entities;
	}

	public List<NoSQLDescriptionEntity> getDescriptionEntities() {
		return new ArrayList<NoSQLDescriptionEntity>(this.entities.values());
	}

	public NoSQLDescriptionEntity getDescriptionEntityByClassName(String className) {
		for (NoSQLDescriptionEntity entityCache : entities.values()) {
			if (className.equalsIgnoreCase(entityCache.getEntityClass().getSimpleName()))
				return entityCache;
		}
		return null;
	}

	public NoSQLDescriptionEntity getDescriptionEntityByCollectionName(String tableName) {
		int count = countDescriptionEntityByCollectionName(tableName);
		if (countDescriptionEntityByCollectionName(tableName) > 1) {
			throw new NoSQLDescriptionEntityManagerException(
					"Foram encontradas " + count + " classes com o mesmo nome de tabela " + tableName);
		}

		if ((tableName != null) && (!"".equals(tableName))) {
			for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
				if ((tableName.equalsIgnoreCase(descriptionEntity.getCollectionName()))
						&& (!descriptionEntity.hasDiscriminatorValue()))
					return descriptionEntity;
			}
		}
		return null;
	}

	public int countDescriptionEntityByCollectionName(String tableName) {
		int result = 0;
		if ((tableName != null) && (!"".equals(tableName))) {
			for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
				if ((tableName.equalsIgnoreCase(descriptionEntity.getCollectionName()))
						&& (!descriptionEntity.hasDiscriminatorValue()))
					result++;
			}
		}
		return result;
	}

	public NoSQLDescriptionEntity getDescriptionEntityByName(String name) {
		if ((name != null) && (!"".equals(name))) {
			for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
				if ((name.equalsIgnoreCase(descriptionEntity.getEntityClass().getName()))
						&& (!descriptionEntity.hasDiscriminatorValue()))
					return descriptionEntity;
			}
		}
		return null;
	}

	public List<NoSQLDescriptionEntity> getDescriptionEntitiesByCollectionName(String collectionName) {
		List<NoSQLDescriptionEntity> result = new ArrayList<NoSQLDescriptionEntity>();
		if ((collectionName != null) && (!"".equals(collectionName))) {
			for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
				if (collectionName.equalsIgnoreCase(descriptionEntity.getCollectionName()))
					result.add(descriptionEntity);
			}
		}
		return result;
	}

	public NoSQLDescriptionEntity[] getAllConcreteDescriptionEntitiesByCollectionName(String collectionName) {
		ArrayList<NoSQLDescriptionEntity> result = new ArrayList<NoSQLDescriptionEntity>();
		for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
			if (collectionName.equalsIgnoreCase(descriptionEntity.getCollectionName())
					&& (!ReflectionUtils.isAbstractClass(descriptionEntity.getEntityClass())))
				result.add(descriptionEntity);
		}
		return result.toArray(new NoSQLDescriptionEntity[] {});
	}

	public NoSQLDescriptionEntity getDescriptionEntity(Class<?> abstractClazz, String discriminatorValue) {
		for (NoSQLDescriptionEntity descriptionEntity : entities.values()) {
			if (discriminatorValue.equals(descriptionEntity.getDiscriminatorValue())
					&& ReflectionUtils.isExtendsClass(abstractClazz, descriptionEntity.getEntityClass()))
				return descriptionEntity;
		}

		return null;
	}

	public boolean isEntity(Object value) {
		return getDescriptionEntity(value.getClass()) != null;
	}

	public boolean isEntity(Class<?> clazz) {
		return getDescriptionEntity(clazz) != null;
	}

	public List<NoSQLDescriptionField> getAllDescriptionFieldBySuperclass(NoSQLDescriptionEntity descriptionEntity) {
		List<NoSQLDescriptionField> fields = new ArrayList<NoSQLDescriptionField>();
		NoSQLDescriptionEntity[] entities = getAllConcreteDescriptionEntitiesByCollectionName(
				descriptionEntity.getCollectionName());
		for (NoSQLDescriptionEntity entity : entities) {
			for (NoSQLDescriptionField descriptionField : entity.getDescriptionFields()) {
				if (!fields.contains(descriptionField))
					fields.add(descriptionField);
			}

		}
		return fields;
	}

	public Class<?> getAnyConcreteClass(Class<?> sourceClass) {
		NoSQLDescriptionEntity sourceDescriptionEntity = getDescriptionEntity(sourceClass);
		if (sourceDescriptionEntity == null)
			return null;
		NoSQLDescriptionEntity[] allConcrete = getAllConcreteDescriptionEntitiesByCollectionName(
				sourceDescriptionEntity.getCollectionName());
		if (allConcrete.length == 0)
			return null;
		else
			return allConcrete[0].getEntityClass();
	}

	/*
	 * Necessário usar o split porque se o valor '$' não existir ele retorna a
	 * String completa getCanonicalName() retorna nulo para objetos anonimos; Se
	 * usasse substring com indexOf, poderia retornar indice -1; Verificar melhor
	 * forma de implementar.
	 */
	public String convertEnumToValue(Enum<?> en) {
		if (en != null) {
			for (NoSQLDescriptionEntity descriptionEntity : getEntities().values()) {
				for (NoSQLDescriptionField descriptionField : descriptionEntity.getDescriptionFields()) {
					if (descriptionField.getGenericType().getTypeName()
							.equals(en.getClass().getName().split("\\$")[0])) { // TODO:
																				// VERIFICAR
																				// FORMA
																				// MAIS
																				// CORRETA
						return descriptionField.getValueEnum(en.toString());
					}
				}
			}
		}
		return en.toString();
	}

	public NoSQLDialect getDialect() {
		return dialect;
	}

	public void setDialect(NoSQLDialect dialect) {
		this.dialect = dialect;
	}

}