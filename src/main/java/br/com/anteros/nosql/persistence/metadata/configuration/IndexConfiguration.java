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
package br.com.anteros.nosql.persistence.metadata.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.anteros.nosql.persistence.utils.IndexType;

public class IndexConfiguration {

	private String name;
	private List<IndexFieldConfiguration> fields = new ArrayList<>();
	private boolean background = false;
    private boolean disableValidation = false;
    private int expireAfterSeconds= -1;
    private String language ="";
    private String languageOverride = "";
    private boolean sparse = false;
    private boolean unique = false;
    private String partialFilter = "";
    
    public IndexConfiguration(String name) {
		this.name = name;
	}
	
	public IndexConfiguration(String name, IndexFieldConfiguration[] fields) {
		this.name = name;
		this.fields = Arrays.asList(fields);
	}
	
	public IndexConfiguration addField(String name, IndexType indexType, int weight) {
		this.fields.add(new IndexFieldConfiguration(name).indexType(indexType).weight(weight));
		return this;
	}

	public String getName() {
		return name;
	}
	
	public IndexConfiguration background(boolean value) {
		this.background = value;
		return this;
	}
	
	public IndexConfiguration disableValidation(boolean value) {
		this.disableValidation = value;
		return this;
	}
	
	public IndexConfiguration expireAfterSeconds(int value) {
		this.expireAfterSeconds = value;
		return this;
	}
	
	public IndexConfiguration language(String value) {
		this.language = value;
		return this;
	}
	
	public IndexConfiguration languageOverride(String value) {
		this.languageOverride = value;
		return this;
	}
	
	public IndexConfiguration sparse(boolean value) {
		this.sparse = value;
		return this;
	}
	
	public IndexConfiguration partialFilter(String value) {
		this.partialFilter = value;
		return this;
	}

	public IndexConfiguration name(String name) {
		this.name = name;
		return this;
	}

	public List<IndexFieldConfiguration> getIndexFields() {
		return fields;
	}

	public IndexConfiguration indexFields(IndexFieldConfiguration[] fields) {
		this.fields = Arrays.asList(fields);
		return this;
	}
	
	public IndexConfiguration indexFields(List<IndexFieldConfiguration> fields) {
		this.fields = fields;
		return this;
	}


	public IndexConfiguration setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isUnique() {
		return unique;
	}

	public IndexConfiguration unique(boolean unique) {
		this.unique = unique;
		return this;
	}

	@Override
	public String toString() {
		String result = name + " => ";
		boolean appendDelimiter = false;
		for (IndexFieldConfiguration f : fields) {
			if (appendDelimiter)
				result += ",";
			result += f;
		}
		return result;
	}

	public List<IndexFieldConfiguration> getFields() {
		return fields;
	}

	public boolean isBackground() {
		return background;
	}

	public boolean isDisableValidation() {
		return disableValidation;
	}

	public int getExpireAfterSeconds() {
		return expireAfterSeconds;
	}

	public String getLanguage() {
		return language;
	}

	public String getLanguageOverride() {
		return languageOverride;
	}

	public boolean isSparse() {
		return sparse;
	}

	public String getPartialFilter() {
		return partialFilter;
	}

}
