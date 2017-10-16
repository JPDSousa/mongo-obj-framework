package org.smof.parsers;

import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.smof.element.Element;

@SuppressWarnings("javadoc")
public class BsonLazyObjectId extends BsonObjectId {
	
	private final String fieldName;
	private final Element element;
	
	public BsonLazyObjectId(String fieldName, Element element) {
		this.fieldName = fieldName;
		this.element = element;
	}

	@Override
	public ObjectId getValue() {
		return null;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Element getElement() {
		return element;
	}

}
