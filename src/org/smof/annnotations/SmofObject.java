package org.smof.annnotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface SmofObject {
	String name();
	boolean required() default false;
	SmofType mapValueType() default SmofType.OBJECT;
}
