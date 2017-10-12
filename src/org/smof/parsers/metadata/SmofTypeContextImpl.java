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
import java.util.Map;
import java.util.Set;

import org.smof.annnotations.ForceInspection;
import org.smof.element.Element;
import org.smof.exception.InvalidTypeException;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.index.InternalIndex;
import org.smof.parsers.BsonParser;
import org.smof.parsers.SmofParserPool;

class SmofTypeContextImpl implements SmofTypeContext {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final Map<Class<?>, TypeStructure<?>> types;
	private final TypeBuilderFactory factory;

	SmofTypeContextImpl() {
		types = new LinkedHashMap<>();
		factory = TypeBuilderFactory.getDefault();
	}

	@Override
	public void put(Class<?> type, SmofParserPool parsers) {
		putWithFactory(type, null, parsers);
	}

	private <T> TypeStructure<T> handleSupertype(Class<T> type, Object factory, SmofParserPool parsers) {
		final TypeBuilder<T> builder = this.factory.create(type, factory);
		final TypeStructure<T> typeStructure = TypeStructure.create(type, builder);
		this.types.put(type, typeStructure);
		handleTypeParser(type, typeStructure, parsers);
		return typeStructure;
	}
	
	private <T> TypeParser<T> handleTypeParser(Class<T> type, TypeStructure<?> typeStructure, SmofParserPool parsers) {
		handleForceInspection(type, typeStructure, parsers);
		if(!type.isInterface()) {
			final TypeParser<T> typeParser = TypeParser.create(type);
			validateParserFields(typeParser, parsers);
			typeStructure.addSubType(type, typeParser);
			return typeParser;
		}
		return null;
	}

	private <T> void handleForceInspection(Class<T> type, TypeStructure<?> typeStructure, SmofParserPool parsers) {
		final ForceInspection annot = type.getAnnotation(ForceInspection.class);
		if(annot != null) {
			for(Class<?> subType : annot.value()) {
				if(type.isAssignableFrom(type)) {
					handleTypeParser(subType, typeStructure, parsers);
				}
			}
		}
	}

	@Override
	public <T> void putWithFactory(Class<T> type, Object factory, SmofParserPool parsers) {
		if(containsSubOrSuperType(type)) {
			handleSubtype(type, parsers);
		}
		else {
			handleSupertype(type, factory, parsers);
		}
	}

	private void handleSubtype(Class<?> type, SmofParserPool parsers) {
		final TypeStructure<?> typeStructure = getTypeStructureFromSub(type);
		handleTypeParser(type, typeStructure, parsers);
	}

	@Override
	public <T> TypeStructure<T> getTypeStructure(Class<T> type, SmofParserPool parsers) {
		return getOrCreateParser(type, parsers);
	}

	@SuppressWarnings("unchecked")
	private <T> TypeStructure<T> getOrCreateParser(Class<?> type, SmofParserPool parsers) {
		final TypeStructure<T> struct = (TypeStructure<T>) getTypeStructureFromSub(type);
		if(struct != null) {
			return putIfAbsent(struct, type, parsers);
		}
		return (TypeStructure<T>) handleSupertype(type, null, parsers);
	}

	private <T> TypeStructure<T> putIfAbsent(TypeStructure<T> struct, Class<?> type, SmofParserPool parsers) {
		if(!struct.containsSub(type)) {
			handleTypeParser(type, struct, parsers);
			return struct;
		}
		return struct;
	}
	
	private TypeStructure<?> getTypeStructureFromSub(Class<?> type) {
		for(Class<?> t : types.keySet()) {
			if(t.isAssignableFrom(type)) {
				return types.get(t);
			}
		}
		return null;
	}

	private boolean containsSubOrSuperType(Class<?> type) {
		for(Class<?> t : types.keySet()) {
			if(t.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	private void checkValidSmofField(SmofField field, SmofParserPool parsers) {
		final BsonParser parser = parsers.get(field.getType());
		if(!parser.isValidType(field)) {
			handleError(new InvalidTypeException(field.getFieldClass(), field.getType()));
		}
	}

	private void validateParserFields(TypeParser<?> parser, SmofParserPool parsers) {
		for(PrimaryField field : parser.getAllFields()) {
			checkValidSmofField(field, parsers);
		}
	}

	@Override
	public <T extends Element> Set<InternalIndex> getIndexes(Class<T> elClass) {
		return types.get(elClass).getIndexes();
	}

}
