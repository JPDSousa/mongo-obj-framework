package org.smof.exception;

@SuppressWarnings("javadoc")
public class MissingRequiredFieldException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public MissingRequiredFieldException(String name) {
		super("Field " + name + " is required but has no value.");
	}

}
