package org.smof.exception;

@SuppressWarnings("javadoc")
public class NotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NotSupportedException(String message) {
		super(message);
	}
	
	public NotSupportedException() {
		super("This functionality is not supported.");
	}

}
