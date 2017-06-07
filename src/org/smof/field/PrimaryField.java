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
package org.smof.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofBoolean;
import org.smof.annnotations.SmofByte;
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
		final SmofByte smofByte;
		final SmofBoolean smofBoolean;
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
		case BYTE:
			smofByte = field.getAnnotation(SmofByte.class);
			name = smofByte.name();
			required = smofByte.required();
			annotation = smofByte;
			break;
		case BOOLEAN:
			smofBoolean = field.getAnnotation(SmofBoolean.class);
			name = smofBoolean.name();
			required = smofBoolean.required();
			annotation = smofBoolean;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PrimaryField other = (PrimaryField) obj;
		if (field == null) {
			if (other.field != null) {
				return false;
			}
		} else if (!field.equals(other.field)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
	
	
}
