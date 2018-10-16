package br.com.anteros.nosql.persistence.session.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public interface NoSQLObjectFactory {

	<T> T createInstance(Class<T> clazz);

	<T> T createInstance(Class<T> clazz, Object noSqlObject);

	Object createInstance(AbstractNoSQLObjectMapper mapper, NoSQLDescriptionField descriptionField,
			Object noSqlObject);

	List<?> createList(NoSQLDescriptionField descriptionField);

	Map<?, ?> createMap(NoSQLDescriptionField descriptionField);

	Set<?> createSet(NoSQLDescriptionField descriptionField);

}
