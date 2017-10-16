package org.smof.parsers.metadata;

import java.util.List;

import org.bson.Document;
import org.smof.field.ParameterField;

@SuppressWarnings("javadoc")
public interface TypeBuilder<T> {

	void setTypes(TypeParser<?> parser);

	T build(Document document);

	List<ParameterField> getParams();
	
}
