package org.smof.dataModel;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.smof.annnotations.SmofIndexField;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface SmofPartialIndex {
	SmofIndexField[] fields();
}
