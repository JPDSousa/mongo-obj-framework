package org.smof.parsers;

import java.lang.annotation.Annotation;

import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofByte;
import org.smof.annnotations.SmofDate;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofObjectId;
import org.smof.annnotations.SmofString;

@SuppressWarnings("javadoc")
public enum SmofType {

	STRING(SmofString.class),
	NUMBER(SmofNumber.class),
	DATETIME(SmofDate.class),
	OBJECT(SmofObject.class),
	OBJECT_ID(SmofObjectId.class),
	ARRAY(SmofArray.class),
	BYTE(SmofByte.class);

	private final Class<? extends Annotation> annotClass;

	private SmofType(Class<? extends Annotation> annotClass) {
		this.annotClass = annotClass;
	}

	public Class<? extends Annotation> getAnnotClass() {
		return annotClass;
	}
}