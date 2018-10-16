package br.com.anteros.nosql.persistence.session.mapping;

public interface NoSQLEntityInterceptor {
	
    void postLoad(Object ent, Object noSQLObject, AbstractNoSQLObjectMapper mapper);

    void postPersist(Object ent, Object noSQLObject, AbstractNoSQLObjectMapper mapper);

    void preLoad(Object ent, Object noSQLObject, AbstractNoSQLObjectMapper mapper);

    void prePersist(Object ent, Object noSQLObject, AbstractNoSQLObjectMapper mapper);

    void preSave(Object ent, Object noSQLObject, AbstractNoSQLObjectMapper mapper);

}
