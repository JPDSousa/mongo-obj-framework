package org.smof.parsers;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;

class BsonBuilder<T> {

	private final Document fields;
	private final Map<Field, Object> addFields;
	private final Map<PrimaryField, ObjectId> lazyElements;

	BsonBuilder() {
		fields = new Document();
		addFields = new LinkedHashMap<>();
		lazyElements = new LinkedHashMap<>();
	}

	void append(String fieldName, Object parsedObject) {
		fields.append(fieldName, parsedObject);
	}
	
	void append2AdditionalFields(Field field, Object parsedObject) {
		addFields.put(field, parsedObject);
	}
	
	void append2LazyElements(PrimaryField field, ObjectId id) {
		lazyElements.put(field, id);
	}

	T build(TypeBuilder<T> builder) {
		return builder.build(fields);
	}

	void fillElement(T element) {
		for(Field field : addFields.keySet()) {
			final Object value = addFields.get(field);
			setValue(element, field, value);
		}
	}

	private void setValue(T element, Field field, final Object value) {
		try {
			field.setAccessible(true);
			field.set(element, value);
			field.setAccessible(false);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SmofException(e);
		}
	}
}
