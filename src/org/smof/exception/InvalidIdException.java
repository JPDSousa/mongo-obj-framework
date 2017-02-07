package org.smof.exception;

import exceptthis.core.AbsCustomException;

@SuppressWarnings("javadoc")
public class InvalidIdException extends AbsCustomException {

	private static final long serialVersionUID = 1L;

	public InvalidIdException(final String elementName, final String id) {
		super("Invalid ID Error", "The id \"%s\" is not valid for the element: %s", id, elementName);
	}
	
	public InvalidIdException(final String id) {
		super("Invalid ID Error", "The id \"%s\" is not valid", id);
	}

}
