package org.smof.parsers.metadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smof.field.PrimaryField;

@SuppressWarnings("javadoc")
public interface TypeParser<T> {
	
	public static <T> TypeParser<T> create(Class<T> type) {
		return new TypeParserImpl<>(type);
	}

	Map<String, PrimaryField> getFieldsAsMap();

	Collection<PrimaryField> getAllFields();

	Set<PrimaryField> getNonBuilderFields();

	Class<T> getType();

}
