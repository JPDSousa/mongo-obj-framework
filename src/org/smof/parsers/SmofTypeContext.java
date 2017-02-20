package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

class SmofTypeContext {
	
	private final Map<Class<?>, AnnotationParser<?>> classParsers;
	
	SmofTypeContext() {
		classParsers = new LinkedHashMap<>();
	}
	
	void put(AnnotationParser<?> parser) {
		classParsers.put(parser.getType(), parser);
	}
	
	@SuppressWarnings("unchecked")
	<T> AnnotationParser<T> getMetadata(Class<T> type) {
		return (AnnotationParser<T>) classParsers.get(type);
	}

}
