package org.smof.element;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.exception.NoSuchAdapterException;

@SuppressWarnings("javadoc")
public class SmofAdapterPool {

	private final Map<Class<?>, SmofAdapter<?>> parsers;
	
	public SmofAdapterPool() {
		parsers = new LinkedHashMap<>();
	}
	
	public <T> void put(SmofAdapter<T> adapter) {
		parsers.put(adapter.getType(), adapter);
	}
	
	@SuppressWarnings("unchecked")
	public <T> SmofAdapter<T> get(Class<T> elClass) throws NoSuchAdapterException {
		final SmofAdapter<T> adapter = (SmofAdapter<T>) parsers.get(elClass);
		if(adapter != null) {
			return adapter;
		}
		throw new NoSuchAdapterException(elClass);
	}
}
