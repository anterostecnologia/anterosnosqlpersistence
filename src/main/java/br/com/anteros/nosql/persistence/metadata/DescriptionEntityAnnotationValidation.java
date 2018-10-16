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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.nosql.persistence.metadata.annotations.DiscriminatorValue;
import br.com.anteros.nosql.persistence.metadata.annotations.Embedded;
import br.com.anteros.nosql.persistence.metadata.annotations.Entity;
import br.com.anteros.nosql.persistence.metadata.annotations.Id;
import br.com.anteros.nosql.persistence.metadata.configuration.EntityConfiguration;
import br.com.anteros.nosql.persistence.metadata.configuration.FieldConfiguration;



public class DescriptionEntityAnnotationValidation {

	public static String[] validateEntityConfiguration(Class<? extends Serializable> sourceClazz,
			EntityConfiguration entityConfiguration) {
		List<String> errors = new ArrayList<String>();

		/*
		 * Se não implementar java.io.Serializable
		 */
		if (!ReflectionUtils.isImplementsInterface(sourceClazz, Serializable.class)) {
			errors.add("A classe " + sourceClazz.getName() + " não implemanta java.io.Serializable");
		}

		/*
		 * Se não existir a configuração Entity 
		 */
		if (!entityConfiguration.isAnnotationPresent(Entity.class) && !entityConfiguration.isAnnotationPresent(Embedded.class)) {
			errors.add("A anotatação Entity ou Embedded não está presente na classe " + sourceClazz.getName());
		}

		/*
		 * Se não existir a configuração Id e não for uma classe Embedded
		 */
		if (!(entityConfiguration.isAnnotationPresent(new Class[] {DiscriminatorValue.class, Embedded.class}))) {
			FieldConfiguration[] fields = entityConfiguration.getAllFields();
			boolean foundAnnotationId = false;
			for (FieldConfiguration field : fields) {
				if ((field.isAnnotationPresent(Id.class))) {
					foundAnnotationId = true;
					break;
				}
			}
			if (!foundAnnotationId)
				errors.add("Toda Entidade deve possuir um Id. Use Id para definir a chave da entidade. "
						+ sourceClazz.getName());		
		}

		if (entityConfiguration.countNumberOfAnnotation(Id.class) > 1)
			errors.add("A Classe " + sourceClazz.getName()
					+ " não pode ter mais de uma configuração Id.");

		return errors.toArray(new String[] {});
	}

	public static String[] validateFieldConfiguration(FieldConfiguration fieldConfiguration) {
		List<String> errors = new ArrayList<String>();

		return errors.toArray(new String[] {});
	}
}
