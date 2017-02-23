package org.smof.field;

import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public interface SmofField {
	
	SmofType getType();
	
	Class<?> getFieldClass();
	
	String getName();

}
