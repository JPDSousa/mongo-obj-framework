package org.smof.collection;

import org.bson.BsonDocument;

import org.smof.element.Element;

import com.mongodb.client.MongoCollection;


@SuppressWarnings("javadoc")
public interface SmofCollection<T extends Element> {
	
	String getCollectionName();
	MongoCollection<BsonDocument> getMongoCollection();
	
	boolean insert(T element);

	void update(T element);

	SmofQuery<T> query();
	
}
