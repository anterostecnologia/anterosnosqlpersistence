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
package br.com.anteros.nosql.persistence.metadata;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import br.com.anteros.nosql.persistence.metadata.type.EntityStatus;
import br.com.anteros.nosql.persistence.session.NoSQLSession;



public class NoSQLEntityManaged {
	private NoSQLDescriptionEntity descriptionEntity;
	private EntityStatus status;
	private Set<String> fieldsForUpdate = new HashSet<String>();
	private Set<NoSQLFieldEntityValue> originalValues = new HashSet<NoSQLFieldEntityValue>();
	private Set<NoSQLFieldEntityValue> lastValues = new HashSet<NoSQLFieldEntityValue>();
	private Object originalVersion;
	private Object oldVersion;
	private Object currentVersion;
	private boolean newEntity;

	public NoSQLEntityManaged(NoSQLDescriptionEntity descriptionEntity) {
		this.descriptionEntity = descriptionEntity;
	}

	public NoSQLDescriptionEntity getDescriptionEntity() {
		return descriptionEntity;
	}

	public void setEntityCache(NoSQLDescriptionEntity descriptionEntity) {
		this.descriptionEntity = descriptionEntity;
	}

	public Set<String> getFieldsForUpdate() {
		return fieldsForUpdate;
	}

	public void setFieldsForUpdate(Set<String> fieldsForUpdate) {
		this.fieldsForUpdate = fieldsForUpdate;
	}

	public Set<NoSQLFieldEntityValue> getOriginalValues() {
		return Collections.unmodifiableSet(originalValues);
	}

	public Set<NoSQLFieldEntityValue> getLastValues() {
		return Collections.unmodifiableSet(lastValues);
	}

	public Object getOriginalVersion() {
		return originalVersion;
	}

	public void setOriginalVersion(Object originalVersion) {
		this.originalVersion = originalVersion;
	}

	public Object getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(Object oldVersion) {
		this.oldVersion = oldVersion;
	}

	public void addOriginalValue(NoSQLFieldEntityValue value) {
		if (value != null) {
			if (originalValues.contains(value))
				originalValues.remove(value);
			originalValues.add(value);
		}
	}

	public void addLastValue(NoSQLFieldEntityValue value) {
		if (value != null) {
			if (lastValues.contains(value))
				lastValues.remove(value);
			lastValues.add(value);
		}
	}

	public void clearLastValues() {
		lastValues.clear();
	}

	public void clearOriginalValues() {
		originalValues.clear();
	}

	public Object getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(Object currentVersion) {
		this.currentVersion = currentVersion;
	}

	public EntityStatus getStatus() {
		return status;
	}

	public void setStatus(EntityStatus status) {
		this.status = status;
	}

	public void updateLastValues(NoSQLSession session, Object targetObject)
			throws Exception {
		this.clearLastValues();
		this.setStatus(EntityStatus.MANAGED);
		for (NoSQLDescriptionField descriptionField : descriptionEntity
				.getDescriptionFields())
			this.addLastValue(descriptionField.getFieldEntityValue(session,
					targetObject));
		this.setOldVersion(this.getCurrentVersion());
		this.setCurrentVersion(null);
	}

	public boolean isNewEntity() {
		return newEntity;
	}

	public void setNewEntity(boolean newEntity) {
		this.newEntity = newEntity;
	}

	public void resetValues() {
		this.clearLastValues();
		for (NoSQLFieldEntityValue field : this.getOriginalValues())
			this.addLastValue(field);
		this.setOldVersion(this.getOriginalVersion());
		this.setCurrentVersion(null);
	}

	public void commitValues() {
		this.clearOriginalValues();
		for (NoSQLFieldEntityValue field : this.getLastValues())
			this.addOriginalValue(field);
		this.setOldVersion(this.getCurrentVersion());
		this.setOriginalVersion(this.getCurrentVersion());
		this.setCurrentVersion(null);
		this.setNewEntity(false);
	}

	
	
	public boolean isVersioned(){
		return descriptionEntity.isVersioned();
	}

}
