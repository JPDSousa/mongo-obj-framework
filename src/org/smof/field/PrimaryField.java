package org.smof.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofDate;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofObjectId;
import org.smof.annnotations.SmofString;
import org.smof.element.Element;
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
	private boolean builder;
	
	public PrimaryField(Field field, SmofType type) throws InvalidSmofTypeException {
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
			external = field.getType().equals(Element.class);
			break;
		case OBJECT_ID:
			smofObjectId = field.getAnnotation(SmofObjectId.class);
			name = smofObjectId.name();
			required = smofObjectId.required();
			annotation = smofObjectId;
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
	
	@Override
	public Class<?> getFieldClass() {
		return getRawField().getType();
	}

	public boolean isBuilder() {
		return builder;
	}

	public void setBuilder(boolean builder) {
		this.builder = builder;
	}
}
