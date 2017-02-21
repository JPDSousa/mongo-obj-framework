package org.smof.collection;

import java.util.stream.Stream;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import org.smof.element.Element;
import org.smof.exception.InvalidIdException;
import org.smof.parsers.SmofParser;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

class SmofCollectionImpl<T extends Element> implements SmofCollection<T> {

	protected final MongoCollection<BsonDocument> collection;
	private final Class<T> type;
	private final String name;
	private final SmofParser parser;

	SmofCollectionImpl(String name, MongoCollection<BsonDocument> collection, Class<T> type, SmofParser parser) {
		this.collection = collection;
		this.name = name;
		this.parser = parser;
		this.type = type;
	}

	@Override
	public boolean insert(final T element) {
		final Document jsonObject;
		final ObjectId id;
		final BsonDocument result = collection.find(getUniqueCondition(element)).first();
		final boolean added = result == null;
		
		try {
			if(result == null) {
//				jsonObject = Document.parse(jsonManager.toJson(element, type));
				id = new ObjectId();
//				jsonObject.append(Element.ID, id);
//				collection.insertOne(jsonObject);
				element.setId(id);
			}
			else {
//				element.setId(result.getObjectId(Element.ID));
			}
		} catch(InvalidIdException e) {
			e.printStackTrace();
		}
		return added;
	}
	
	@Override
	public SmofQuery<T> query() {
		final FindIterable<BsonDocument> rawQuery = collection.find();
		return new SmofQuery<T>(type, rawQuery, parser);
	}
	
	protected final Stream<T> find(Bson condition) {
		final FindIterable<BsonDocument> result;
		if(condition == null) {
			result = collection.find();
		} else {
			result = collection.find(condition);
		}
		
//		return StreamSupport.stream(result.spliterator(), false)
//				.map(d -> jsonManager.fromJson(d.toJson(), type));
		return null;
	}

//	@Override
//	public T lookup(final ObjectId id) {
//		final Document result = collection.find(Filters.eq(Element.ID, id)).first();
//				
//		return jsonManager.fromJson(result.toJson(), type);
//	}

//	@Override
//	public Set<T> lookupAll(final Iterable<T> ids) {
//		final Set<T> elements = new LinkedHashSet<>();
//		ids.forEach(i -> elements.add(lookup(i.getId())));
//		
//		return elements;
//	}

	protected Bson getUniqueCondition(T element) {
		return null;
	}
	
	@Override
	public void update(final T element) {
		final Bson query = Filters.eq(Element.ID, element.getId());
//		final Document doc = Document.parse(jsonManager.toJson(element, type));
//		
//		collection.findOneAndReplace(query, doc);
	}
	
//	@Override
//	public T get(final T element) {
//		final Document result = collection.find(getUniqueCondition(element)).first();
//		
//		if(result == null) {
//			return null;
//		}
//		return jsonManager.fromJson(result.toJson(), type);
//	}

	@Override
	public String getCollectionName() {
		return name;
	}

	@Override
	public MongoCollection<BsonDocument> getMongoCollection() {
		return collection;
	}

}
