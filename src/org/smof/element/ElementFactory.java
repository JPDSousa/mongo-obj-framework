package org.smof.element;

@SuppressWarnings("javadoc")
public interface ElementFactory<E extends Element> {

	E createEmptyElement(final String id) throws InvalidIdException;
}
