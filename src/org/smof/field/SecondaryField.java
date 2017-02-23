package org.smof.field;

import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class SecondaryField implements SmofField {

	private final SmofType type;
	private final String name;
	private final Class<?> fieldClass;
	
	public SecondaryField(String name, SmofType type, Class<?> fieldClass) {
		super();
		this.type = type;
		this.name = name;
		this.fieldClass = fieldClass;
	}

	@Override
	public SmofType getType() {
		return type;
	}

	@Override
	public Class<?> getFieldClass() {
		return fieldClass;
	}

	@Override
	public String getName() {
		return name;
	}

}
