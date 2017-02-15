package org.smof.exception;

import org.smof.element.field.SmofField;

@SuppressWarnings("javadoc")
public class InvalidTypeException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	public InvalidTypeException(Class<?> invalidType, SmofField.FieldType type) {
		super("The type " + invalidType.getName() + " is not valid for a field type " + type.getAnnotClass().getName());
	}

}
