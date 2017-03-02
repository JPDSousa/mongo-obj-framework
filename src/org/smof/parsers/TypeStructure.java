package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexes;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.index.InternalIndex;

class TypeStructure<T> {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final TypeBuilder<T> defaultTypeBuilder;
	private final Map<Class<?>, TypeParser<?>> subTypes;
	private final Map<String, PrimaryField> allFields;
	private final Class<T> type;
	private final Set<InternalIndex> indexes;

	TypeStructure(Class<T> type, TypeBuilder<T> builder) {
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
		final Map<String, List<PrimaryField>> indexedFields = getIndexedFields();
		final SmofIndex[] indexNotes = getIndexNotes();
		for(SmofIndex indexNote : indexNotes) {
			final List<PrimaryField> fields = indexedFields.get(indexNote.key());
			indexes.add(new InternalIndex(indexNote, fields));
		}
	}

	private SmofIndex[] getIndexNotes() {
		return type.getAnnotation(SmofIndexes.class).value();
	}
	
	private Map<String, List<PrimaryField>> getIndexedFields() {
		return allFields.values().stream()
				.filter(f -> f.hasIndex())
				.collect(Collectors.groupingBy(f -> f.getIndexKey()));
	}
	
	Set<InternalIndex> getIndexes() {
		return indexes;
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

	TypeBuilder<T> getBuilder() {
		return defaultTypeBuilder;
	}

	@SuppressWarnings("unchecked")
	<E> TypeParser<E> getParser(Class<E> type) {
		return (TypeParser<E>) subTypes.get(type);
	}

	public boolean containsSub(Class<?> type) {
		return subTypes.containsKey(type);
	}
}