/*******************************************************************************
 * Copyright (C) 2017 Joao
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.parsers;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.parsers.metadata.TypeBuilder;

final class BsonBuilder<T> {

	private final Document fields;
	private final Map<Field, Object> addFields;
	private final Map<PrimaryField, ObjectId> lazyElements;

	protected BsonBuilder() {
		fields = new Document();
		addFields = new LinkedHashMap<>();
		lazyElements = new LinkedHashMap<>();
	}

	protected void append(String fieldName, Object parsedObject) {
		fields.append(fieldName, parsedObject);
	}
	
	protected void append2AdditionalFields(Field field, Object parsedObject) {
		addFields.put(field, parsedObject);
	}
	
	protected void append2LazyElements(PrimaryField field, ObjectId id) {
		lazyElements.put(field, id);
	}

	protected T build(TypeBuilder<T> builder) {
		return builder.build(fields);
	}

	protected void fillElement(T element) {
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
