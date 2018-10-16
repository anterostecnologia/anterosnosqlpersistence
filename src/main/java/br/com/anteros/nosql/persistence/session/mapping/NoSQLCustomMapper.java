package br.com.anteros.nosql.persistence.session.mapping;

import java.util.Map;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.cache.NoSQLEntityCache;

public interface NoSQLCustomMapper {
	
    public void fromDocument(final NoSQLSession<?> session, final Object dbObject, final NoSQLDescriptionField descriptionField, final Object entity,
                             final NoSQLEntityCache cache, final AbstractNoSQLObjectMapper mapper) throws Exception;

    public void toDocument(final Object entity, final NoSQLDescriptionField descriptionField, final Object dbObject, final Map<Object, Object> involvedObjects,
                           final AbstractNoSQLObjectMapper mapper) throws Exception;

}
