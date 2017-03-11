package org.smof.annnotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@SuppressWarnings("javadoc")
@Retention(RUNTIME)
@Target(FIELD)
public @interface SmofByte {
	String name();
	boolean required() default false;
}
