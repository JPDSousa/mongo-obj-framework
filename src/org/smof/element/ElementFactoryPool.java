package org.smof.element;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("javadoc")
public class ElementFactoryPool {

	private final Map<Class<? extends Element>, ElementFactory<? extends Element>> pool;
	
	public ElementFactoryPool() {
		pool = new LinkedHashMap<>();
	}
	
	public <T extends Element> void put(Class<T> type, ElementFactory<T> factory) {
		pool.put(type, factory);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Element> ElementFactory<T> get(Class<T> type) {
		return (ElementFactory<T>) pool.get(type);
	}
}
