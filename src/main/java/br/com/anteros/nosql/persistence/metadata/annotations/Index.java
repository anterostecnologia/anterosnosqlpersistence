package br.com.anteros.nosql.persistence.metadata.annotations;



import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
public @interface Index {
	
	String name() default ""; 

	IndexField[] fields() default {};

    IndexOptions options() default @IndexOptions();
    
   	@Target({ FIELD, TYPE })
   	@Retention(RUNTIME)
   	@Documented
   	public @interface List {
   		Index[] value();
   	}
   

}
