package org.smof.annnotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import org.smof.dataModel.SmofQueryA;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface SmofPFEQuery {
	String name();
	SmofQueryA[] expression();
}
