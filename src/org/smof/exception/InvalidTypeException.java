package org.smof.exception;

import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class InvalidTypeException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	public InvalidTypeException(Class<?> invalidType, SmofType type) {
		super("The type " + invalidType.getName() + " is not valid for a field type " + type.getAnnotClass().getName());
	}

}
