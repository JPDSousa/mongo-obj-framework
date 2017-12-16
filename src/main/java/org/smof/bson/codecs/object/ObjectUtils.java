package org.smof.bson.codecs.object;

import java.lang.reflect.Field;

import org.smof.exception.MissingRequiredFieldException;
import org.smof.field.PrimaryField;

@SuppressWarnings("javadoc")
public abstract class ObjectUtils {

	private ObjectUtils() {}
	
	public static Object extractValue(Object element, PrimaryField field) {
		try {
			final Field rawField = field.getRawField();
			final Object value;
			rawField.setAccessible(true);
			value = rawField.get(element);
			rawField.setAccessible(false);
			return value;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void checkRequired(PrimaryField field, Object value) {
		if(field.isRequired() && value == null) {
			final String name = field.getName();
			throw new RuntimeException(new MissingRequiredFieldException(name));
		}
	}
}
