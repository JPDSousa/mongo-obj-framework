package org.smof.annnotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.smof.index.IndexType;

@SuppressWarnings("javadoc")
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface SmofIndexField {
	String name();
	IndexType type();
}
