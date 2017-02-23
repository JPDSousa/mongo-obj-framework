package org.smof.annnotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface SmofIndex {
	String key();
	boolean unique() default false;
}
