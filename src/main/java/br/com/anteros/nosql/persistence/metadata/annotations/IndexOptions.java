package br.com.anteros.nosql.persistence.metadata.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface IndexOptions {

	boolean background() default false;

    boolean disableValidation() default false;

    int expireAfterSeconds() default -1;

    String language() default "";

    String languageOverride() default "";

    String name() default "";

    boolean sparse() default false;

    boolean unique() default false;

    String partialFilter() default "";

}
