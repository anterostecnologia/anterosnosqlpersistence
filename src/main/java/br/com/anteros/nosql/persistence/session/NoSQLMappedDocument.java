package br.com.anteros.nosql.persistence.session;

public interface NoSQLMappedDocument {

	public boolean hasId() throws Exception;

	public boolean hasNonNullId();

	public Object getId() throws Exception;

	public <T> T getId(Class<T> type) throws Exception;

	public boolean isIdPresent(Class<?> type);
	
	public Object getDocument();
}
