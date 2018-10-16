package br.com.anteros.nosql.persistence.metadata.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Version {
	
	String value() default AbstractNoSQLObjectMapper.IGNORED_FIELDNAME;
}
