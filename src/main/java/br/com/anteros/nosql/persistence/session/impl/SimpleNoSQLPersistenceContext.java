/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package br.com.anteros.nosql.persistence.session.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.utils.AnterosWeakHashMap;
import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntity;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionEntityManager;
import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;
import br.com.anteros.nosql.persistence.metadata.NoSQLEntityManaged;
import br.com.anteros.nosql.persistence.metadata.type.EntityStatus;
import br.com.anteros.nosql.persistence.session.NoSQLPersistenceContext;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.cache.Cache;
import br.com.anteros.nosql.persistence.session.cache.NoSQLCache;


public class SimpleNoSQLPersistenceContext implements NoSQLPersistenceContext {
	private static Logger LOG = LoggerProvider.getInstance().getLogger(NoSQLPersistenceContext.class);

	
	private Map<Object, NoSQLEntityManaged> entities = new AnterosWeakHashMap<Object, NoSQLEntityManaged>();
	private NoSQLDescriptionEntityManager descriptionEntityManager;
	private NoSQLSession session;
	private Cache cache;

	public SimpleNoSQLPersistenceContext(NoSQLSession session, NoSQLDescriptionEntityManager descriptionEntityManager) {
		this.descriptionEntityManager = descriptionEntityManager;
		this.session = session;
		this.cache = new NoSQLCache();
	}

	public NoSQLEntityManaged addEntityManaged(Object value, boolean readOnly, boolean newEntity, boolean checkIfExists)
			throws Exception {
		LOG.debug("Add entity managed ");
		

		NoSQLEntityManaged entityManaged = null;
		if (checkIfExists)
			entityManaged = getEntityManaged(value);
		
		if (entityManaged == null || !checkIfExists) {
			LOG.debug("Create new entity managed");
			NoSQLDescriptionEntity descriptionEntity = descriptionEntityManager.getDescriptionEntity(value.getClass());
			entityManaged = new NoSQLEntityManaged(descriptionEntity);
			entityManaged.setStatus(readOnly ? EntityStatus.READ_ONLY : EntityStatus.MANAGED);
			entityManaged.setNewEntity(newEntity);

			if (!readOnly) {
				entityManaged.setFieldsForUpdate(descriptionEntity.getAllFieldNames());
				for (NoSQLDescriptionField descriptionField : descriptionEntity.getDescriptionFields())
					entityManaged.addLastValue(descriptionField.getFieldEntityValue(session, value));
			}
			entities.put(value, entityManaged);
			LOG.debug("Entity managed created");
		}
		return entityManaged;
	}

	public NoSQLEntityManaged getEntityManaged(Object key) {
		return entities.get(key);
	}

	public void removeEntityManaged(Object key) {
		entities.remove(key);
	}

	public boolean isExistsEntityManaged(Object key) {
		return entities.containsKey(key);
	}

	public void onBeforeExecuteCommit(NoSQLConnection connection) throws Exception {
		session.onBeforeExecuteCommit(connection);
	}

	public void onBeforeExecuteRollback(NoSQLConnection connection) throws Exception {
		session.onBeforeExecuteRollback(connection);
	}

	public void onAfterExecuteCommit(NoSQLConnection connection) throws Exception {
		if (session.getConnection() == connection) {
			for (NoSQLEntityManaged entityManaged : entities.values())
				entityManaged.commitValues();
		}
	}

	public void onAfterExecuteRollback(NoSQLConnection connection) throws Exception {
		if (session.getConnection() == connection) {
			for (NoSQLEntityManaged entityManaged : entities.values())
				entityManaged.resetValues();
			removeNewEntities();
		}
	}

	private void removeNewEntities() {
		List<NoSQLEntityManaged> entitiesToRemove = new ArrayList<NoSQLEntityManaged>();
		for (NoSQLEntityManaged entityManaged : entities.values()) {
			if (entityManaged.isNewEntity())
				entitiesToRemove.add(entityManaged);
		}
		for (NoSQLEntityManaged entityManaged : entitiesToRemove)
			entities.remove(entityManaged);
	}

	public Object getObjectFromCache(Object key) {
		return cache.get(key);
	}

	public void addObjectToCache(Object key, Object value) {
		cache.put(key, value);
	}

	public void addObjectToCache(Object key, Object value, int secondsToLive) {
		cache.put(key, value, secondsToLive);
	}

	public NoSQLEntityManaged createEmptyEntityManaged(Object key) {
		NoSQLEntityManaged em = new NoSQLEntityManaged(descriptionEntityManager.getDescriptionEntity(key.getClass()));
		entities.put(key, em);
		return em;
	}

	public void evict(Class sourceClass) {
		List<Object> keys = new ArrayList<Object>(entities.keySet());
		for (Object obj : keys) {
			if (obj != null) {
				if (obj.getClass().equals(sourceClass)) {
					entities.remove(obj);
				}
			}
		}
	}

	public void evictAll() {
		entities.clear();
	}

	public void clearCache() {
		cache.clear();
	}

	@Override
	public void detach(Object entity) {
		List<Object> keys = new ArrayList<Object>(entities.keySet());
		for (Object obj : keys) {
			if (obj != null) {
				if (obj.equals(entity)) {
					entities.remove(obj);
					break;
				}
			}
		}
	}

	@Override
	public boolean isWithoutTransactionControl() {
		return session.isWithoutTransactionControl();
	}

}
