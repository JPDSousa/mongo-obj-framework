package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;

class TypeStructure<T> {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final TypeBuilder<T> defaultTypeBuilder;
	private final Map<Class<?>, TypeParser<?>> subTypes;
	private final Map<String, SmofType> allFields;
	private final Class<T> type;

	TypeStructure(Class<T> type, TypeBuilder<T> builder) {
		this.type = type;
		checkValidBuilder(builder);
		this.subTypes = new LinkedHashMap<>();
		this.allFields = new LinkedHashMap<>();
		this.defaultTypeBuilder = builder;
	}

	private void checkValidBuilder(TypeBuilder<T> builder) {
		if(builder == null) {
			handleError(new IllegalArgumentException("SmofBuilder not found for type " + type.getName()));
		}
	}

	<E> void addSubType(Class<E> subType, TypeParser<E> parser) {
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
				allFields.put(name, field.getType());
			}
		}
	}

	private void checkValidType(String name, SmofType type) {
		if(!allFields.get(name).equals(type)) {
			handleError(new InvalidSmofTypeException("Smof type " + type 
					+ " is not consistent for field " + name + " in type " + this.type.getName()));
		}
	}

	private void checkValidParser(TypeParser<?> parser) {
		if(parser == null) {
			handleError(new IllegalArgumentException("Must specify a parser."));
		}
	}

	TypeBuilder<T> getBuilder() {
		return defaultTypeBuilder;
	}

	@SuppressWarnings("unchecked")
	<E> TypeParser<E> getParser(Class<E> type) {
		return (TypeParser<E>) subTypes.get(type);
	}
}