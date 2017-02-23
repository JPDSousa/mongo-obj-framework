package org.smof.annnotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface SmofNumber {
	public String name();
	public long defaultValue() default 0;
	public boolean required() default false;
	
	public String indexKey() default "";
}
