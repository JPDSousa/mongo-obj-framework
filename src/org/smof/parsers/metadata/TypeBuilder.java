package org.smof.parsers.metadata;

import java.util.List;

import org.bson.Document;
import org.smof.field.ParameterField;

@SuppressWarnings("javadoc")
public interface TypeBuilder<T> {

	public void setTypes(TypeParser<?> parser);

	public T build(Document document);

	public List<ParameterField> getParams();
	
}
