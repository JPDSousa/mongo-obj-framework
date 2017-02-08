package org.smof.element;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.smof.element.field.SmofField;
import org.smof.element.field.SmofObjectId;

@SuppressWarnings("javadoc")
public class ElementParser {
	
	private static ElementParser singleton;
	
	public static ElementParser getDefault() {
		if(singleton == null) {
			singleton = new ElementParser();
		}
		return singleton;
	}
	
	public Map<SmofField, Field> getSmofFields(Class<?> elClass) {
		return Arrays.stream(elClass.getDeclaredFields())
				.filter(f -> Arrays.stream(SmofField.values()).anyMatch(ff -> f.isAnnotationPresent(ff.getAnnotClass())))
				.collect(Collectors.toMap(f -> SmofField.getFieldType(f.getAnnotations()), f -> f));
	}

	public Set<Field> getExternalFields(Class<? extends Element> elClass) {
		return Arrays.stream(elClass.getDeclaredFields())
				.filter(f -> f.getType().equals(Element.class) && f.isAnnotationPresent(SmofObjectId.class))
				.collect(Collectors.toSet());
	}
}
