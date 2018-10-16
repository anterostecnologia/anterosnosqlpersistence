package br.com.anteros.nosql.persistence.metadata.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

	Class<?> concreteClass() default Object.class;

	String value() default AbstractNoSQLObjectMapper.IGNORED_FIELDNAME;

	String defaultValue() default "";
	
	boolean required() default false;

}
