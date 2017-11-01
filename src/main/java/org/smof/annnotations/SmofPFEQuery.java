package org.smof.annnotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@SuppressWarnings("javadoc")
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface SmofPFEQuery {
	String name();
	SmofFilter[] query();
}
