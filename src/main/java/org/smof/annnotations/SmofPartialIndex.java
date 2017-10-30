package org.smof.annnotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.smof.annnotations.SmofIndexField;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SmofPartialIndex {
	SmofIndexField[] fields();
	SmofPFEQuery pfe();
}
