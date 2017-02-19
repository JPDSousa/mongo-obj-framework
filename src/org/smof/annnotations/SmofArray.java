package org.smof.annnotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface SmofArray {
	public String name();
	public SmofType type();
	public boolean required() default false;
	public String[] defaultValue() default {};
	public String[] validValues() default {};
}
