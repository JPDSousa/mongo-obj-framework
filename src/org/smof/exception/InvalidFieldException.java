package org.smof.exception;

@SuppressWarnings("javadoc")
public class InvalidFieldException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidFieldException() {
		super("None is not a valid field.");
	}
}
