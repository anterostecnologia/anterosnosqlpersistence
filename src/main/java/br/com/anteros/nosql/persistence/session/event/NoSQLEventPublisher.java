package br.com.anteros.nosql.persistence.session.event;

public interface NoSQLEventPublisher<T> {

	void publishEvent(NoSQLEvent<T> event);

}
