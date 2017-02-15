package org.smof.exception;

@SuppressWarnings("javadoc")
public class UnsupportedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UnsupportedException(String message) {
		super(message);
	}
	
	public UnsupportedException() {
		super("This functionality is not supported.");
	}

}
