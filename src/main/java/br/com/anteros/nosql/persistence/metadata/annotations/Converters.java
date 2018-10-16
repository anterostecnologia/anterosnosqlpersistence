package br.com.anteros.nosql.persistence.metadata.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.anteros.nosql.persistence.converters.NoSQLTypeConverter;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Converters {
 
    Class<? extends NoSQLTypeConverter>[] value();
}
