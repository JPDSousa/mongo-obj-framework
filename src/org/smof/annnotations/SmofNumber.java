package org.smof.annnotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.smof.index.IndexType;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface SmofNumber {
	public String name();
	public boolean required() default false;
	
	String indexKey() default "";
	IndexType indexType() default IndexType.ASCENDING;
}
