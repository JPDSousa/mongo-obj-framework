package org.smof.field;

import java.lang.reflect.Parameter;

import org.smof.annnotations.SmofParam;
import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class ParameterField implements SmofField {
	
	private final Parameter parameter;
	private final SmofParam note;
	private PrimaryField primaryField;

	public ParameterField(Parameter parameter, SmofParam note) {
		this.parameter = parameter;
		this.note = note;
	}

	@Override
	public SmofType getType() {
		return primaryField == null ? null : primaryField.getType();
	}
	
	public PrimaryField getPrimaryField() {
		return primaryField;
	}
	
	public void setPrimaryField(PrimaryField field) {
		this.primaryField = field;
	}

	@Override
	public Class<?> getFieldClass() {
		return parameter.getType();
	}

	@Override
	public String getName() {
		return note.name();
	}

}
