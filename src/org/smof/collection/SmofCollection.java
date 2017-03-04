package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;


@SuppressWarnings("javadoc")
public interface SmofCollection<T extends Element> {
	
	String getCollectionName();
	MongoCollection<BsonDocument> getMongoCollection();
	
	void insert(T element);

	void execUpdate(Bson filter, Bson updates, UpdateOptions options);
	SmofUpdate<T> update();
	void replace(T element);

	SmofQuery<T> query();
	T findById(ObjectId id);
	
	SmofParser getParser();
	Class<T> getType();
	
}
