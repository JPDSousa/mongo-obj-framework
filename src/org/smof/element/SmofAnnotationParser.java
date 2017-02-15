package org.smof.element;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.smof.element.field.SmofField;
import org.smof.exception.InvalidSmofTypeException;

@SuppressWarnings("javadoc")
public class SmofAnnotationParser<T> {
	
	private final Class<T> type;
	
	private final SortedSet<SmofField> fields;
	
	public SmofAnnotationParser(Class<T> type) throws InvalidSmofTypeException {
		this.type = type;
		fields = new TreeSet<>();
		
		fillFields();
	}
	
	public Class<T> getType() {
		return type;
	}

	private void fillFields() throws InvalidSmofTypeException {
		for(Field field : type.getDeclaredFields()) {
			for(SmofField.FieldType fieldType : SmofField.FieldType.values()) {
				if(field.isAnnotationPresent(fieldType.getAnnotClass())) {
					fields.add(new SmofField(field, fieldType));
					break;
				}
			}
		}
	}
	
	public Set<SmofField> getFields() {
		return fields;
	}
	
	public Set<SmofField> getExternalFields() {
		return getFields().stream()
				.filter(f -> f.isExternal())
				.collect(Collectors.toSet());
	}
	
	public Set<SmofField> getReadableFields() {
		return getFields().stream()
				.filter(f -> !f.isManualRead())
				.collect(Collectors.toSet());
	}

//	public Set<Field> getExternalFields(Class<? extends Element> elClass) {
//		return Arrays.stream(elClass.getDeclaredFields())
//				.filter(f -> f.getType().equals(Element.class) && f.isAnnotationPresent(SmofObjectId.class))
//				.collect(Collectors.toSet());
//	}
}
