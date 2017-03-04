package org.smof.collection;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.parsers.SmofParser;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

class SmofCollectionImpl<T extends Element> implements SmofCollection<T> {

	private static final int CACHE_SIZE = 200;
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	protected final MongoCollection<BsonDocument> collection;
	private final Class<T> type;
	private final String name;
	private final SmofParser parser;
	private final Cache<ObjectId, T> cache;

	SmofCollectionImpl(String name, MongoCollection<BsonDocument> collection, Class<T> type, SmofParser parser) {
		this.collection = collection;
		this.name = name;
		this.parser = parser;
		this.type = type;
		cache = CacheBuilder.newBuilder()
				.maximumSize(CACHE_SIZE)
				.expireAfterAccess(5, TimeUnit.MINUTES)
				.build();
	}

	@Override
	public void insert(final T element) {
		if(!cache.asMap().containsKey(element.getId())) {
			final BsonDocument document = parser.toBson(element);
			collection.insertOne(document);
			cache.put(element.getId(), element);
		}
	}
	
	@Override
	public SmofQuery<T> query() {
		final FindIterable<BsonDocument> rawQuery = collection.find();
		return new SmofQuery<T>(type, rawQuery, parser, cache);
	}
	
	@Override
	public T findById(ObjectId id) {
		try {
			return cache.get(id, new IdLoader(id));
		} catch (ExecutionException e) {
			handleError(e);
			return null;
		}
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
	
	private class IdLoader implements Callable<T> {

		private final ObjectId id;
		
		private IdLoader(ObjectId id) {
			super();
			this.id = id;
		}

		@Override
		public T call() throws Exception {
			final Bson idFilter = Filters.eq(Element.ID, id);
			final FindIterable<BsonDocument> query = collection.find(idFilter);
			return parser.fromBson(query.first(), type);
		}
		
	}

	@Override
	public SmofParser getParser() {
		return parser;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

}
