package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.exception.InvalidTypeException;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;

class SmofTypeContext {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final Map<Class<?>, TypeStructure<?>> types;

	SmofTypeContext() {
		types = new LinkedHashMap<>();
	}

	void put(Class<?> type, SmofParserPool parsers) {
		putWithFactory(type, null, parsers);
	}

	private <T> TypeStructure<T> handleSupertype(Class<T> type, Object factory, SmofParserPool parsers) {
		final TypeBuilder<T> builder = TypeBuilder.create(type, factory);
		final TypeStructure<T> typeStructure = new TypeStructure<>(type, builder);
		this.types.put(type, typeStructure);
		handleTypeParser(type, typeStructure, parsers);
		return typeStructure;
	}
	
	private <T> TypeParser<T> handleTypeParser(Class<T> type, TypeStructure<?> typeStructure, SmofParserPool parsers) {
		if(!type.isInterface()) {
			final TypeParser<T> typeParser = TypeParser.create(type);
			validateParserFields(typeParser, parsers);
			typeStructure.addSubType(type, typeParser);
			return typeParser;
		}
		return null;
	}

	<T> void putWithFactory(Class<T> type, Object factory, SmofParserPool parsers) {
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

	<T> TypeParser<T> getTypeParser(Class<T> type, SmofParserPool parsers) {
		return getOrCreateParser(type, parsers);
	}

	private <T> TypeParser<T> getOrCreateParser(Class<T> type, SmofParserPool parsers) {
		final TypeStructure<?> struct = getTypeStructureFromSub(type);
		if(struct != null) {
			return putIfAbsent(struct, type, parsers);
		}
		return handleSupertype(type, null, parsers).getParser(type);
	}

	private <T> TypeParser<T> putIfAbsent(TypeStructure<?> struct, Class<T> type, SmofParserPool parsers) {
		if(!struct.containsSub(type)) {
			return handleTypeParser(type, struct, parsers);
		}
		return struct.getParser(type);
	}

	<T> TypeBuilder<T> getTypeBuilder(Class<T> type, SmofParserPool parsers) {
		return getOrCreateBuilder(type, null, parsers);
	}

	private <T> TypeBuilder<T> getOrCreateBuilder(Class<T> type, Object factory, SmofParserPool parsers) {
		checkValidSuperType(type);
		final TypeStructure<T> struct = getTypeStructureFromSuper(type);
		if(struct != null) {
			return struct.getBuilder();
		}
		return handleSupertype(type, factory, parsers).getBuilder();
	}
	
	private TypeStructure<?> getTypeStructureFromSub(Class<?> type) {
		for(Class<?> t : types.keySet()) {
			if(t.isAssignableFrom(type)) {
				return types.get(t);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> TypeStructure<T> getTypeStructureFromSuper(Class<T> type) {
		if(!types.containsKey(type)) {
			return null;
		}
		return (TypeStructure<T>) types.get(type);
	}

	private boolean containsSubOrSuperType(Class<?> type) {
		for(Class<?> t : types.keySet()) {
			if(t.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsSuperType(Class<?> type) {
		return types.containsKey(type);
	}

	private void checkValidSuperType(Class<?> type) {
		if(!containsSuperType(type) && containsSubOrSuperType(type)) {
			handleError(new IllegalArgumentException(type.getName() + " is not a valid super type."));
		}
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

}
