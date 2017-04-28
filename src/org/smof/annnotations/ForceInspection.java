package org.smof.annnotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@SuppressWarnings("javadoc")
@Retention(RUNTIME)
@Target(TYPE)
public @interface ForceInspection {

	Class<?>[] value();
}
