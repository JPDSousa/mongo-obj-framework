package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;

import com.mongodb.client.model.Filters;

@SuppressWarnings("javadoc")
public class SmofUpdateQuery<T extends Element> {

	private final BsonDocument update;
	private final SmofCollection<T> collection;
	private Bson filter;
	private final SmofUpdateOptions options;
	
	SmofUpdateQuery(BsonDocument udpate, SmofCollection<T> collection, SmofUpdateOptions options) {
		super();
		this.update = udpate;
		this.collection = collection;
		filter = new BsonDocument();
		this.options = options;
	}
	
	public SmofUpdateQuery<T> withId(ObjectId id) {
		filter = Filters.eq(Element.ID, id);
		return this;
	}
	
	public void execute() {
		collection.execUpdate(filter, update, options);
	}
	
}
