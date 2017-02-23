package org.smof.field;

import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class MasterField implements SmofField {

	private final SmofType type;
	private final Class<?> elementClass;
	
	public MasterField(Class<?> elementClass) {
		type = SmofType.OBJECT;
		this.elementClass = elementClass;
	}
	
	@Override
	public SmofType getType() {
		return type;
	}

	@Override
	public Class<?> getFieldClass() {
		return elementClass;
	}

	@Override
	public String getName() {
		return elementClass.getName();
	}

}
