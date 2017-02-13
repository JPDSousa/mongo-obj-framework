package org.smof.element;

import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public interface ElementFactory<E extends Element> {

	E createElement(final ElementFieldMap map) throws SmofException;
}
