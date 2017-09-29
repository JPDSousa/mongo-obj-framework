package org.smof.annnotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.smof.annnotations.SmofIndexField;
import org.smof.collection.SmofQuery;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SmofPartialIndex {
	SmofIndexField[] fields();
	SmofPFEQuery pfe();
}
