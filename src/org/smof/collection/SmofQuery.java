package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.mongodb.client.FindIterable;

@SuppressWarnings("javadoc")
public class SmofQuery<T extends Element> {

	private final Class<T> elementClass;
	
	private Bson conditions;
	
	public SmofQuery(Class<T> elementClass) {
		this.elementClass = elementClass;
	}
	
	public Class<T> getElementClass() {
		return elementClass;
	}
	
	public Bson toBson() {
		return conditions;
	}

	SmofResults<T> results(FindIterable<BsonDocument> find, SmofParser parser) {
		return new SmofResults<T>(find, parser, elementClass);
	}
}
