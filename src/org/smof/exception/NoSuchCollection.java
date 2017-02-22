package org.smof.exception;

import org.smof.element.Element;

@SuppressWarnings("javadoc")
public class NoSuchCollection extends Throwable {

	private static final long serialVersionUID = 1L;

	public NoSuchCollection(Class<? extends Element> elementClass) {
		super("No class registered for element " + elementClass.getName());
	}
	
}
