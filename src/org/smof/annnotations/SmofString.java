package org.smof.annnotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface SmofString {
	String name();
	boolean required() default false;
	
	String indexKey() default "";
}
