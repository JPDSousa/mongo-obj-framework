package org.smof.annnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javax.lang.model.element.Element;

import org.smof.exception.InvalidSmofTypeException;
import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class PrimaryField implements Comparable<PrimaryField>, SmofField{

	private final SmofType type;
	private final String name;
	private final boolean required;
	private final Annotation annotation;
	private final Field field;
	private final boolean external;
	private final boolean builderField;

	public PrimaryField(Field field, SmofType type, List<String> builderFields) throws InvalidSmofTypeException {
		final SmofArray smofArray;
		final SmofDate smofDate;
		final SmofNumber smofNumber;
		final SmofObject smofObject;
		final SmofObjectId smofObjectId;
		final SmofString smofString;
		boolean external = false;

		switch(type) {
		case ARRAY:
			smofArray = field.getAnnotation(SmofArray.class);
			name = smofArray.name();
			required = smofArray.required();
			annotation = smofArray;
			break;
		case DATETIME:
			smofDate = field.getAnnotation(SmofDate.class);
			name = smofDate.name();
			required = smofDate.required();
			annotation = smofDate;
			break;
		case NUMBER:
			smofNumber = field.getAnnotation(SmofNumber.class);
			name = smofNumber.name();
			required = smofNumber.required();
			annotation = smofNumber;
			break;
		case OBJECT:
			smofObject = field.getAnnotation(SmofObject.class);
			name = smofObject.name();
			required = smofObject.required();
			annotation = smofObject;
			break;
		case OBJECT_ID:
			smofObjectId = field.getAnnotation(SmofObjectId.class);
			name = smofObjectId.name();
			required = smofObjectId.required();
			annotation = smofObjectId;
			external = field.getType().equals(Element.class);
			break;
		case STRING:
			smofString = field.getAnnotation(SmofString.class);
			name = smofString.name();
			required = smofString.required();
			annotation = smofString;
			break;
		default:
			throw new InvalidSmofTypeException("Type not valid.");
		}
		this.field = field;
		this.type = type;
		this.external = external;
		this.builderField = builderFields.contains(name);
	}

	@Override
	public SmofType getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isRequired() {
		return required;
	}

	public <T extends Annotation> T getSmofAnnotationAs(Class<T> annotationType) {
		return annotationType.cast(annotation);
	}

	public Field getRawField() {
		return field;
	}

	@Override
	public int compareTo(PrimaryField o) {
		final int boolCompare = Boolean.compare(this.isRequired(), o.isRequired());
		return boolCompare == 0 ? String.CASE_INSENSITIVE_ORDER.compare(this.name, o.name) : boolCompare;
	}

	public boolean isExternal() {
		return external;
	}

	public boolean isBuilderField() {
		return builderField;
	}
	
	@Override
	public Class<?> getFieldClass() {
		return getRawField().getType();
	}
}
