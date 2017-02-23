package org.smof.annnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SmofIndexes {
	SmofIndex[] value();
}
