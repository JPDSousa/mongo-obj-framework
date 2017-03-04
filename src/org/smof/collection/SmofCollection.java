package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.mongodb.client.MongoCollection;


@SuppressWarnings("javadoc")
public interface SmofCollection<T extends Element> {
	
	String getCollectionName();
	MongoCollection<BsonDocument> getMongoCollection();
	
	void insert(T element);

	void execUpdate(Bson filter, Bson updates, SmofUpdateOptions options);
	SmofUpdate<T> update();
	void replace(T element, SmofUpdateOptions options);

	SmofQuery<T> query();
	T findById(ObjectId id);
	
	SmofParser getParser();
	Class<T> getType();
	
}
