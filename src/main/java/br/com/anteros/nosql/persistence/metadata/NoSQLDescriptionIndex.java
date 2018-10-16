package br.com.anteros.nosql.persistence.metadata;

import java.util.ArrayList;
import java.util.List;

public class NoSQLDescriptionIndex {
	
	private String name;
	private List<NoSQLDescriptionField> fields = new ArrayList<>();
	private boolean background = false;
    private boolean disableValidation = false;
    private int expireAfterSeconds= -1;
    private String language ="";
    private String languageOverride = "";
    private boolean sparse = false;
    private boolean unique = false;
    private String partialFilter = "";
	private NoSQLDescriptionEntity descriptionEntity;
    
    
    
	public NoSQLDescriptionIndex(NoSQLDescriptionEntity descriptionEntity) {
		this.descriptionEntity = descriptionEntity;
	}
	public String getName() {
		return name;
	}
	public NoSQLDescriptionIndex setName(String name) {
		this.name = name;
		return this;
	}
	public List<NoSQLDescriptionField> getFields() {
		return fields;
	}
	public NoSQLDescriptionIndex setFields(List<NoSQLDescriptionField> fields) {
		this.fields = fields;
		return this;
	}
	public boolean isBackground() {
		return background;
	}
	public NoSQLDescriptionIndex setBackground(boolean background) {
		this.background = background;
		return this;
	}
	public boolean isDisableValidation() {
		return disableValidation;
	}
	public NoSQLDescriptionIndex setDisableValidation(boolean disableValidation) {
		this.disableValidation = disableValidation;
		return this;
	}
	public int getExpireAfterSeconds() {
		return expireAfterSeconds;
	}
	public NoSQLDescriptionIndex setExpireAfterSeconds(int expireAfterSeconds) {
		this.expireAfterSeconds = expireAfterSeconds;
		return this;
	}
	public String getLanguage() {
		return language;
	}
	public NoSQLDescriptionIndex setLanguage(String language) {
		this.language = language;
		return this;
	}
	public String getLanguageOverride() {
		return languageOverride;
	}
	public NoSQLDescriptionIndex setLanguageOverride(String languageOverride) {
		this.languageOverride = languageOverride;
		return this;
	}
	public boolean isSparse() {
		return sparse;
	}
	public NoSQLDescriptionIndex setSparse(boolean sparse) {
		this.sparse = sparse;
		return this;
	}
	public boolean isUnique() {
		return unique;
	}
	public NoSQLDescriptionIndex setUnique(boolean unique) {
		this.unique = unique;
		return this;
	}
	public String getPartialFilter() {
		return partialFilter;
	}
	public NoSQLDescriptionIndex setPartialFilter(String partialFilter) {
		this.partialFilter = partialFilter;
		return this;
	}
	@Override
	public String toString() {
		return "\n	NoSQLDescriptionIndex [name=" + name + ", fields=" + fields + ", background=" + background
				+ ", disableValidation=" + disableValidation + ", expireAfterSeconds=" + expireAfterSeconds
				+ ", language=" + language + ", languageOverride=" + languageOverride + ", sparse=" + sparse
				+ ", unique=" + unique + ", partialFilter=" + partialFilter + ", descriptionEntity=" + descriptionEntity
				+ "]";
	}

}
