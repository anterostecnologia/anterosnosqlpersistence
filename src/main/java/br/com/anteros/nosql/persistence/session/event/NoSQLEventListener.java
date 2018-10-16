package br.com.anteros.nosql.persistence.session.event;

public interface NoSQLEventListener<T> {
	
	public void onEvent(NoSQLEvent<T> event);

}
