package org.smof.collection;

import org.bson.Document;

import org.smof.element.Element;
import org.smof.query.SmofQuery;
import org.smof.query.SmofResults;

import com.mongodb.client.MongoCollection;


@SuppressWarnings("javadoc")
public interface SmofCollection<T extends Element> {
	
	String getCollectionName();
	MongoCollection<Document> getMongoCollection();
	
	boolean insert(T element);

	void update(T element);

	SmofResults<T> find(SmofQuery<T> query);
	
}
