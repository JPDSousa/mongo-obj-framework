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
package org.smof.parsers.metadata;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexes;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.index.InternalIndex;
import org.smof.parsers.SmofType;

class TypeStructureImpl<T> implements TypeStructure<T>{

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final TypeBuilder<T> defaultTypeBuilder;
	private final Map<Class<?>, TypeParser<?>> subTypes;
	private final Map<String, PrimaryField> allFields;
	private final Class<T> type;
	private final Set<InternalIndex> indexes;

	TypeStructureImpl(Class<T> type, TypeBuilder<T> builder) {
		this.type = type;
		checkValidBuilder(builder);
		this.subTypes = new LinkedHashMap<>();
		this.allFields = new LinkedHashMap<>();
		this.defaultTypeBuilder = builder;
		this.indexes = new LinkedHashSet<>();
		if(hasIndexes()) {
			fillIndexes();	
		}
	}

	private void checkValidBuilder(TypeBuilder<T> builder) {
		if(builder == null) {
			handleError(new IllegalArgumentException("SmofBuilder not found for type " + type.getName()));
		}
	}
	
	private boolean hasIndexes() {
		return type.isAnnotationPresent(SmofIndexes.class);
	}

	private void fillIndexes() {
		final SmofIndex[] indexNotes = getIndexNotes();
		for(SmofIndex indexNote : indexNotes) {
			indexes.add(InternalIndex.fromSmofIndex(indexNote));
		}
	}

	private SmofIndex[] getIndexNotes() {
		return type.getAnnotation(SmofIndexes.class).value();
	}
	
	@Override
	public Set<InternalIndex> getIndexes() {
		return indexes;
	}

	@Override
	public <E> void addSubType(Class<E> subType, TypeParser<E> parser) {
		checkValidParser(parser);
		checkTypeConsistency(parser);
		defaultTypeBuilder.setTypes(parser);
		subTypes.put(subType, parser);
	}


	private void checkTypeConsistency(TypeParser<?> parser) {
		for(PrimaryField field : parser.getAllFields()) {
			final String name = field.getName();
			if(allFields.containsKey(name)) {
				checkValidType(name, field.getType());
			}
			else {
				allFields.put(name, field);
			}
		}
	}

	private void checkValidType(String name, SmofType type) {
		if(!allFields.get(name).getType().equals(type)) {
			handleError(new InvalidSmofTypeException("Smof type " + type 
					+ " is not consistent for field " + name + " in type " + this.type.getName()));
		}
	}

	private void checkValidParser(TypeParser<?> parser) {
		if(parser == null) {
			handleError(new IllegalArgumentException("Must specify a parser."));
		}
	}

	@Override
	public TypeBuilder<T> getBuilder() {
		return defaultTypeBuilder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> TypeParser<E> getParser(Class<E> type) {
		return (TypeParser<E>) subTypes.get(type);
	}

	@Override
	public boolean containsSub(Class<?> type) {
		return subTypes.containsKey(type);
	}

	@Override
	public Map<String, PrimaryField> getAllFields() {
		return allFields;
	}
}
