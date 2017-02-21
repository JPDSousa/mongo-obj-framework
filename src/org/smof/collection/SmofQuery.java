package org.smof.collection;

import org.bson.BsonDocument;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

@SuppressWarnings("javadoc")
public class SmofQuery<T extends Element> {

	private final Class<T> elementClass;
	private final FindIterable<BsonDocument> rawQuery;
	private final SmofParser parser;
	
	SmofQuery(Class<T> elementClass, FindIterable<BsonDocument> rawQuery, SmofParser parser) {
		this.elementClass = elementClass;
		this.rawQuery = rawQuery;
		this.parser = parser;
	}
	
	public Class<T> getElementClass() {
		return elementClass;
	}

	public SmofResults<T> results() {
		return new SmofResults<T>(rawQuery, parser, elementClass);
	}

	public void withField(String fieldName, String artistName) {
		rawQuery.filter(Filters.eq(fieldName, artistName));
		
	}
	
	
}
