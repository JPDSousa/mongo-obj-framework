package org.smof.element;

@SuppressWarnings("javadoc")
public interface ElementFactory<E extends Element> {

	E createElement(final ElementFieldMap map);
}
