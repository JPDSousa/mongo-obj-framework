package org.smof.element.field;

import java.lang.annotation.Annotation;

@SuppressWarnings("javadoc")
public enum Fields {
	
	STRING(SmofString.class),
	NUMBER(SmofNumber.class),
	DATE(SmofDate.class),
	OBJECT(SmofInnerObject.class),
	OBJECT_ID(SmofObjectId.class),
	ARRAY(SmofArray.class);
	
	private final Class<? extends Annotation> annotClass;
	
	private Fields(Class<? extends Annotation> annotClass) {
		this.annotClass = annotClass;
	}

	public Class<? extends Annotation> getAnnotClass() {
		return annotClass;
	}

	public static Fields getFieldType(Annotation[] annotations) {
		for(Annotation annotation : annotations) {
			for(Fields f : values()) {
				if(annotation.annotationType().equals(f.getAnnotClass())) {
					return f;
				}
			}
		}
		return null;
	}
}
