package org.smof.exception;

@SuppressWarnings("javadoc")
public class UnsupportedBsonException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UnsupportedBsonException(String message) {
		super(message);
	}
	
	public UnsupportedBsonException() {
		super("This functionality is not supported.");
	}

}
