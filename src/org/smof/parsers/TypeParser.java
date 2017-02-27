package org.smof.parsers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

@SuppressWarnings("javadoc")
public class TypeParser<T> {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	static <T> TypeParser<T> create(Class<T> type) {
		try {
			return new TypeParser<>(type);
		} catch (InvalidSmofTypeException e) {
			handleError(e);
			return null;
		}
	}

	private final Class<T> type;

	private final Map<String, PrimaryField> fields;
	
	private final Set<InternalIndex> indexes;

	private TypeParser(Class<T> type) throws InvalidSmofTypeException {
		this.type = type;
		this.fields = new LinkedHashMap<>();
		this.indexes = new LinkedHashSet<>();
		fillFields();
		if(hasIndexes()) {
			fillIndexes();	
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
		return fields.values().stream()
				.filter(f -> f.hasIndex())
				.collect(Collectors.groupingBy(f -> f.getIndexKey()));
	}

	private void fillFields() throws InvalidSmofTypeException {
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

	public Class<T> getType() {
		return type;
	}

	public Collection<PrimaryField> getAllFields() {
		return getFieldsAsMap().values();
	}
	
	public Map<String, PrimaryField> getFieldsAsMap() {
		return fields;
	}
	
	public Set<InternalIndex> getIndexes() {
		return indexes;
	}

	public Set<PrimaryField> getNonBuilderFields() {
		return getAllFields().stream()
				.filter(f -> !f.isBuilder())
				.collect(Collectors.toSet());
	}

	public Set<PrimaryField> getExternalFields() {
		return getAllFields().stream()
				.filter(f -> f.isExternal())
				.collect(Collectors.toSet());
	}
}
