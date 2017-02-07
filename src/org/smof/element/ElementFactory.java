package org.smof.element;

import org.smof.exception.InvalidIdException;

@SuppressWarnings("javadoc")
public interface ElementFactory<E extends Element> {

	E createEmptyElement(final String id) throws InvalidIdException;
}
