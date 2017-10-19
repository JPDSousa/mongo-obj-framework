/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
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
package org.smof.parsers.metadata;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.smof.field.PrimaryField;
import org.smof.parsers.SmofType;

import com.google.common.collect.Maps;

class TypeParserImpl<T> implements TypeParser<T> {

	private static final long serialVersionUID = 1L;

	private final Class<T> type;

	private transient Map<String, PrimaryField> fields;

	TypeParserImpl(Class<T> type) {
		this.type = type;
		this.fields = new LinkedHashMap<>();
		fillFields();
	}

	private void fillFields() {
		PrimaryField current;
		for(Field field : getDeclaredFields(type)) {
			for(SmofType fieldType : SmofType.values()) {
				if(field.isAnnotationPresent(fieldType.getAnnotClass())) {
					current = new PrimaryField(field, fieldType);
					this.fields.put(current.getName(), current);
					break;
				}
			}
		}
	}

	private List<Field> getDeclaredFields(Class<?> type) {
		final List<Field> fields = new ArrayList<>();
		
		if(type != null) {
			final Field[] classFields = type.getDeclaredFields();
			fields.addAll(Arrays.asList(classFields));
			fields.addAll(getDeclaredFields(type.getSuperclass()));
		}
		
		return fields;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public Collection<PrimaryField> getAllFields() {
		return getFieldsAsMap().values();
	}
	
	@Override
	public Map<String, PrimaryField> getFieldsAsMap() {
		return fields;
	}

	@Override
	public Set<PrimaryField> getNonBuilderFields() {
		return getAllFields().stream()
				.filter(f -> !f.isBuilder())
				.collect(Collectors.toSet());
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		fields = Maps.newLinkedHashMap();
		fillFields();
	}

}
