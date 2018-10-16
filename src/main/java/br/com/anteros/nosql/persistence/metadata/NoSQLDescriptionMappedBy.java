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

/**
 * Classe responsável pela Descricao do MappedBy 
 *
 */
public class NoSQLDescriptionMappedBy {

	private NoSQLDescriptionEntity descriptionEntity;
	private NoSQLDescriptionField descriptionField;
	private String mappedBy;

	public NoSQLDescriptionMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}

	public void setDescriptionEntity(NoSQLDescriptionEntity descriptionEntity) {
		this.descriptionEntity = descriptionEntity;
		setDescriptionField(descriptionEntity.getDescriptionFieldByFieldName(mappedBy));
	}

	public NoSQLDescriptionEntity getDescriptionEntity() {
		return descriptionEntity;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}
	
	@Override
	public String toString() {
		return "NoSQLDescriptionMappedBy [mappedBy=" + mappedBy + ", descriptionEntity=" + descriptionEntity
				+ ", descriptionField=" + descriptionField + "]";
	}

	public NoSQLDescriptionField getDescriptionField() {
		return descriptionField;
	}

	public void setDescriptionField(NoSQLDescriptionField descriptionField) {
		this.descriptionField = descriptionField;
	}

}
