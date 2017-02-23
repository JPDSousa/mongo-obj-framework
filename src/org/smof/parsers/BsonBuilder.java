package org.smof.parsers;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.smof.annnotations.PrimaryField;
import org.smof.element.AbstractElement;
import org.smof.exception.SmofException;

class BsonBuilder<T> {

	private final Document builderFields;
	private final Map<Field, Object> additionalFields;

	BsonBuilder() {
		builderFields = new Document();
		additionalFields = new LinkedHashMap<>();
	}

	private void append2Builder(String fieldName, Object parsedObject) {
		builderFields.append(fieldName, parsedObject);
	}

	private void append2AdditionalFields(Field field, Object parsedObject) {
		additionalFields.put(field, parsedObject);
	}

	void append(PrimaryField field, Object parsedObject) {
		final Field rawField = field.getRawField();
		final String name = field.getName();
		if(field.isBuilderField()) {
			append2Builder(name, parsedObject);
		}
		else {
			append2AdditionalFields(rawField, parsedObject);
		}
	}

	T build(AnnotationParser<T> metadata) {
		try {
			final T element = metadata.createSmofObject(builderFields);
			fillElement(element);
			return element;
		} catch(IllegalAccessException | IllegalArgumentException e) {
			throw new SmofException(e);
		}
	}

	private void fillElement(T element) throws IllegalArgumentException, IllegalAccessException {
		for(Field field : additionalFields.keySet()) {
			final Object value = additionalFields.get(field);
			field.setAccessible(true);
			field.set(element, value);
			field.setAccessible(false);
		}
	}
}
