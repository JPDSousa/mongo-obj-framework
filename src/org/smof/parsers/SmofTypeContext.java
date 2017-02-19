package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.exception.InvalidSmofTypeException;

@SuppressWarnings("javadoc")
public class SmofTypeContext {
	
	private final Map<Class<?>, AnnotationParser<?>> classParsers;
	
	SmofTypeContext() {
		classParsers = new LinkedHashMap<>();
	}
	
	public <T> void put(Class<T> type, Object factory) throws InvalidSmofTypeException {
		final AnnotationParser<T> parser = new AnnotationParser<>(type, factory);
		put(parser);
	}
	
	public <T> void put(Class<T> type) throws InvalidSmofTypeException {
		final AnnotationParser<T> parser = new AnnotationParser<>(type);
		put(parser);
	}
	
	private <T> void put(AnnotationParser<T> parser) {
		classParsers.put(parser.getType(), parser);
	}
	
	@SuppressWarnings("unchecked")
	public <T> AnnotationParser<T> getFields(Class<T> type) {
		return (AnnotationParser<T>) classParsers.get(type);
	}

}
