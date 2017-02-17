package org.smof.annnotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.smof.annnotations.SmofField.FieldType;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface SmofArray {
	public String name();
	public FieldType type();
	public boolean required() default false;
	public String[] defaultValue() default {};
	public String[] validValues() default {};
}
