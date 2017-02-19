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
	public String name();
	public boolean required() default false;
	public SmofType mapValueType() default SmofType.OBJECT;
}
