package org.smof.element.field;

import java.lang.annotation.Annotation;

@SuppressWarnings("javadoc")
public enum SmofField {
	
	STRING(SmofString.class),
	NUMBER(SmofNumber.class),
	DATE(SmofDate.class),
	OBJECT(SmofInnerObject.class),
	OBJECT_ID(SmofObjectId.class),
	ARRAY(SmofArray.class);
	
	private final Class<? extends Annotation> annotClass;
	
	private SmofField(Class<? extends Annotation> annotClass) {
		this.annotClass = annotClass;
	}

	public Class<? extends Annotation> getAnnotClass() {
		return annotClass;
	}

	public static SmofField getFieldType(Annotation[] annotations) {
		for(Annotation annotation : annotations) {
			for(SmofField f : values()) {
				if(annotation.annotationType().equals(f.getAnnotClass())) {
					return f;
				}
			}
		}
		return null;
	}
}
