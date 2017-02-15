package org.smof.exception;

@SuppressWarnings("javadoc")
public class NoSuchAdapterException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NoSuchAdapterException(Class<?> objClass) {
		super("No adapter registered for " + objClass.getName() + ".");
	}

}
