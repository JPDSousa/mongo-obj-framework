package org.smof.query;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.smof.element.Element;

import com.google.gson.Gson;
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

	public SmofResults<T> results(FindIterable<Document> find, Gson jsonManager) {
		return new SmofResults<T>(find, jsonManager, elementClass);
	}
}
