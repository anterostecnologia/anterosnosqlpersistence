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
public @interface Reference {

    boolean idOnly() default false;

    boolean ignoreMissing() default false;

    boolean lazy() default false;
    
    String value() default AbstractNoSQLObjectMapper.IGNORED_FIELDNAME;
    
    String mappedBy() default "";
    
    boolean required() default false;

}
