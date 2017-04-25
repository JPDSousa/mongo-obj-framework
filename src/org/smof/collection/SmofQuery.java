package org.smof.collection;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.google.common.cache.Cache;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

@SuppressWarnings("javadoc")
public class SmofQuery<T extends Element> extends AbstractSmofQuery<T, SmofQuery<T>>{

	private final Cache<ObjectId, T> cache;
	private final MongoCollection<BsonDocument> collection;
	
	SmofQuery(Class<T> elementClass, SmofParser parser, Cache<ObjectId, T> cache, MongoCollection<BsonDocument> collection) {
		super(parser, elementClass);
		this.cache = cache;
		this.collection = collection;
	}
	
	@Override
	public SmofResults<T> results() {
		return new SmofResults<T>(getParser(), getElementClass(), cache, collection, getFilter());
	}
	
	public AndQuery<T> beginAnd() {
		return new AndQuery<>(this);
	}
	
	public OrQuery<T> beginOr() {
		return new OrQuery<>(this);
	}
	
	@Override
	public SmofQuery<T> applyBsonFilter(Bson filter) {
		final BsonDocument filterDoc = filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
		for(Map.Entry<String, BsonValue> entry : filterDoc.entrySet()) {
			getFilter().append(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public T byElement(T element) {
		return withField(Element.ID, element.getId())
		.results()
		.first();
	}
	
}
