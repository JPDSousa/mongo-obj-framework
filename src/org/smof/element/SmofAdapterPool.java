package org.smof.element;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.NoSuchAdapterException;

@SuppressWarnings("javadoc")
public class SmofAdapterPool {

	private final Map<Class<?>, SmofAdapter<?>> parsers;
	
	public SmofAdapterPool() {
		parsers = new LinkedHashMap<>();
	}
	
	public <T> SmofAdapter<T> put(Class<T> type, Object factory) throws InvalidSmofTypeException {
		final SmofAnnotationParser<T> parser = new SmofAnnotationParser<>(type, factory);
		return put(parser);
	}
	
	public <T> SmofAdapter<T> put(Class<T> type) throws InvalidSmofTypeException {
		final SmofAnnotationParser<T> parser = new SmofAnnotationParser<>(type);
		return put(parser);
	}
	
	private <T> SmofAdapter<T> put(SmofAnnotationParser<T> parser) {
		final SmofAdapter<T> adapter = new SmofAdapter<>(parser, this); 
		parsers.put(parser.getType(), adapter);
		return adapter;
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
