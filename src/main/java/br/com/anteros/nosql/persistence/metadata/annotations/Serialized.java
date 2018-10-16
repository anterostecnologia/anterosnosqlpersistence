package br.com.anteros.nosql.persistence.metadata.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.anteros.nosql.persistence.session.mapping.AbstractNoSQLObjectMapper;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serialized {

    boolean disableCompression() default false;
    
    String value() default AbstractNoSQLObjectMapper.IGNORED_FIELDNAME;

}
