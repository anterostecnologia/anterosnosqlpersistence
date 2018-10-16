package br.com.anteros.nosql.persistence.metadata.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.nosql.persistence.metadata.annotations.DiscriminatorField;
import br.com.anteros.nosql.persistence.metadata.annotations.DiscriminatorValue;
import br.com.anteros.nosql.persistence.metadata.annotations.Embedded;
import br.com.anteros.nosql.persistence.metadata.annotations.Entity;
import br.com.anteros.nosql.persistence.metadata.annotations.Lob;
import br.com.anteros.nosql.persistence.metadata.annotations.Property;
import br.com.anteros.nosql.persistence.metadata.annotations.Temporal;
import br.com.anteros.nosql.persistence.metadata.annotations.type.TemporalType;
import br.com.anteros.nosql.persistence.metadata.comparator.DependencyComparator;
import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;


public class NoSQLPersistenceModelConfiguration {

		private Map<Class<? extends Serializable>, EntityConfiguration> entities = new LinkedHashMap<Class<? extends Serializable>, EntityConfiguration>();

		public EntityConfiguration addEntity(Class<? extends Serializable> sourceClazz) {
			EntityConfiguration entity = new EntityConfiguration(sourceClazz, this);
			entity.loadAnnotations();
			entities.put(sourceClazz, entity);
			return entity;
		}
		
		public EnumConfiguration addEnum(Class<? extends Serializable> sourceClazz) {
			EnumConfiguration enumConfiguration = new EnumConfiguration(sourceClazz, this);
			enumConfiguration.loadAnnotations();
			entities.put(sourceClazz, enumConfiguration);
			return enumConfiguration;
		}


		public Map<Class<? extends Serializable>, EntityConfiguration> getEntities() {
			return entities;
		}

		public void loadAnnotationsByClass(Class<? extends Serializable> sourceClazz)
				throws InstantiationException, IllegalAccessException {
				addEntity(sourceClazz);
		}

		public void sortByDependency() {
			List<Class<? extends Serializable>> clazzes = new ArrayList<Class<? extends Serializable>>(entities.keySet());
			Collections.sort(clazzes, new DependencyComparator());
			Map<Class<? extends Serializable>, EntityConfiguration> newEntities = new LinkedHashMap<Class<? extends Serializable>, EntityConfiguration>();
			for (Class<? extends Serializable> sourceClazz : clazzes)
				newEntities.put(sourceClazz, entities.get(sourceClazz));
			entities = newEntities;
		}

		public void createOmmitedOrDefaultSettings() {
			sortByDependency();

			for (Class<?> sourceClazz : getEntities().keySet()) {
				EntityConfiguration entityConfiguration = getEntities().get(sourceClazz);
				for (FieldConfiguration fieldConfiguration : entityConfiguration.getFields()) {

					/*
					 * Verifica configuração básica
					 */
					checkBasicConfigurations(fieldConfiguration);
					
					/*
					 * Verifica se é uma collection e não foi definido nenhuma configuração
					 */
					if (fieldConfiguration.isCollection() && !fieldConfiguration.isTransient()) {
						if (fieldConfiguration.getAnnotations().size()==0) {
							fieldConfiguration.embedded(null, fieldConfiguration.getName());
						}
						
					}
					
					/*
					 * Verifica se é um map e não foi definido nenhuma configuração
					 */
					if (fieldConfiguration.isMap() && !fieldConfiguration.isTransient()) {
						if (fieldConfiguration.getAnnotations().size()==0) {
							fieldConfiguration.embedded(null, fieldConfiguration.getName());
						}						
					}
				}
			}

			for (Class<?> sourceClazz : getEntities().keySet()) {
				EntityConfiguration entityConfiguration = getEntities().get(sourceClazz);

				EntityConfiguration entityConfigurationBySuperClass = getEntityConfigurationBySourceClass(
						sourceClazz.getSuperclass());
				/*
				 * Se não foi definido collectionName e não é uma herança ou uma classe embedded define o padrão
				 */
				if (!entityConfiguration.isAnnotationPresent(new Class[] { Entity.class, Embedded.class })
						&& (entityConfigurationBySuperClass == null) && StringUtils.isEmpty(entityConfiguration.getCollectionName())) {
					entityConfiguration.collectionName(entityConfiguration.getSourceClazz().getSimpleName());
				}

				/*
				 * Se é uma classe abstrata e não foi definido @DiscriminatorColumn
				 * define o padrão
				 */
				if (ReflectionUtils.isAbstractClass(entityConfiguration.getSourceClazz())
						&& !entityConfiguration.isAnnotationPresent(DiscriminatorField.class)) {
					entityConfiguration.discriminatorField("discriminatorType");
				}				

				/*
				 * Se é uma classe herdada e não foi definido o @DiscriminatorValue
				 */
				if (!entityConfiguration.isAnnotationPresent(DiscriminatorValue.class) 
						&& (entityConfigurationBySuperClass != null)) {
					entityConfiguration.discriminatorValue(sourceClazz.getSimpleName());
				}
				
				
				
			}
		}
	

		protected void checkBasicConfigurations(FieldConfiguration fieldConfiguration) {
			if (fieldConfiguration.isTransient())
				return;

			/*
			 * Se não foi definido nome do campo ou definido campo como transient e
			 * não é uma coleção.
			 */
			if (!fieldConfiguration.isAnnotationPresent(new Class[] { Property.class })
					&& (fieldConfiguration.isSimpleField())) {
				fieldConfiguration.name(fieldConfiguration.getField().getName());
			}


			/*
			 * Se o campo é um Lob e não foi definido @Lob
			 */
			if (ReflectionUtils.isLobField(fieldConfiguration.getField())
					&& !fieldConfiguration.isAnnotationPresent(Lob.class)) {
				fieldConfiguration.lob(AbstractNoSQLObjectMapper.IGNORED_FIELDNAME);
			}

			/*
			 * Se o campo é uma Data e não foi definido @Temporal
			 */
			if (ReflectionUtils.isDateTimeField(fieldConfiguration.getField())
					&& !fieldConfiguration.isAnnotationPresent(Temporal.class)) {
				fieldConfiguration.temporal(TemporalType.DATE,AbstractNoSQLObjectMapper.IGNORED_FIELDNAME);
			}			
			
		}

		

		public EntityConfiguration getEntityConfigurationBySourceClass(Class<?> sourceClazz) {
			return getEntities().get(sourceClazz);
		}


	}