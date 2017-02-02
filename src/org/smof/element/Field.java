package org.smof.element;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface Field {
	String name();
	Type type();
	String ref() default "";
	Type arrayType() default Type.NONE;
	boolean required() default false;
	String[] valid();//needs to be an iterable
}
