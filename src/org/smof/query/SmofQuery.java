package org.smof.query;

import org.smof.element.Element;

@SuppressWarnings("javadoc")
public class SmofQuery<T extends Element> {

	private final Class<T> elementClass;
	
	public SmofQuery(Class<T> elementClass) {
		this.elementClass = elementClass;
	}
	
	public Class<T> getElementClass() {
		return elementClass;
	}
}
