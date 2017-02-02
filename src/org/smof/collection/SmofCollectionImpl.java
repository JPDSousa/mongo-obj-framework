package org.smof.collection;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import org.smof.element.Element;
import org.smof.element.InvalidIdException;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

class SmofCollectionImpl<T extends Element> implements SmofCollection<T> {

	protected final MongoCollection<Document> collection;
	protected final Gson jsonManager;
	private final Class<T> type;

	protected SmofCollectionImpl(final Gson jsonManager, final MongoCollection<Document> collection, final Class<T> type) {
		this.collection = collection;
		this.jsonManager = jsonManager;
		this.type = type;
	}

	@Override
	public boolean insert(final T element) {
		final Document jsonObject;
		final ObjectId id;
		final Document result = collection.find(getUniqueCondition(element)).first();
		final boolean added = result == null;
		
		try {
			if(result == null) {
				jsonObject = Document.parse(jsonManager.toJson(element, type));
				id = new ObjectId();
				jsonObject.append(Element.ID, id);
				collection.insertOne(jsonObject);
				element.setId(id);
			}
			else {
				element.setId(result.getObjectId(Element.ID));
			}
		} catch(InvalidIdException e) {
			e.printStackTrace();
		}
		return added;
	}
	
	@Override
	public Stream<T> getAll() {
		return find(null);
	}
	
	protected final Stream<T> find(Bson condition) {
		final FindIterable<Document> result;
		if(condition == null) {
			result = collection.find();
		} else {
			result = collection.find(condition);
		}
		
		return StreamSupport.stream(result.spliterator(), false)
				.map(d -> jsonManager.fromJson(d.toJson(), type));
	}

	@Override
	public T lookup(final ObjectId id) {
		final Document result = collection.find(Filters.eq(Element.ID, id)).first();
				
		return jsonManager.fromJson(result.toJson(), type);
	}

	@Override
	public Set<T> lookupAll(final Iterable<T> ids) {
		final Set<T> elements = new LinkedHashSet<>();
		ids.forEach(i -> elements.add(lookup(i.getId())));
		
		return elements;
	}

	protected Bson getUniqueCondition(T element) {
		return null;
	}
	
	@Override
	public void update(final T element) {
		final Bson query = Filters.eq(Element.ID, element.getId());
		final Document doc = Document.parse(jsonManager.toJson(element, type));
		
		collection.findOneAndReplace(query, doc);
	}
	
	@Override
	public T get(final T element) {
		final Document result = collection.find(getUniqueCondition(element)).first();
		
		if(result == null) {
			return null;
		}
		return jsonManager.fromJson(result.toJson(), type);
	}

}
