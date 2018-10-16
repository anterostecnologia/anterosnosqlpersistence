package br.com.anteros.nosql.persistence.session.query;

public interface NoSQLQuery<T> {
	
	public T getQueryObject();

	public T getFieldsObject();

	public T getSortObject();

	public NoSQLQuery<T> offSet(long offSet);

	public NoSQLQuery<T> limit(int limit);

	public NoSQLQuery<T> with(Pageable pageable);

	public NoSQLQuery<T> with(Sort sort);
	
	public NoSQLQuery<T> withHint(String hint);

	public long getOffset();

	public int getLimit();
	
	public String getHint();
	
	


}
