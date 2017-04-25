package org.smof.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.index.InternalIndex;
import org.smof.parsers.SmofParser;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReturnDocument;

class SmofCollectionImpl<T extends Element> implements SmofCollection<T> {

	private static final int CACHE_SIZE = 200;

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	protected final MongoCollection<BsonDocument> collection;
	private final Class<T> type;
	private final String name;
	private final SmofParser parser;
	private final Set<InternalIndex> indexes;
	private final Cache<ObjectId, T> cache;

	SmofCollectionImpl(String name, MongoCollection<BsonDocument> collection, Class<T> type, SmofParser parser) {
		this.collection = collection;
		this.name = name;
		this.parser = parser;
		this.type = type;
		this.indexes = parser.getIndexes(type);
		cache = CacheBuilder.newBuilder()
				.maximumSize(CACHE_SIZE)
				.expireAfterAccess(5, TimeUnit.MINUTES)
				.build();
		updateIndexes();
	}

//	private Set<InternalIndex> loadIndexes() {
//		final Set<InternalIndex> indexes = new LinkedHashSet<>();
//		final ListIndexesIterable<BsonDocument> bsonIndexes = collection.listIndexes(BsonDocument.class);
//		for(BsonDocument bsonIndex : bsonIndexes) {
//			indexes.add(InternalIndex.fromBson(bsonIndex));
//		}
//		return indexes;
//	}

	private void updateIndexes() {
		final Collection<InternalIndex> newIndexes = CollectionUtils.removeAll(parser.getIndexes(type), indexes);
		for(InternalIndex index : newIndexes) {
			collection.createIndex(index.getIndex(), index.getOptions());
		}
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
		return new SmofQuery<T>(type, parser, cache, collection);
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

	@Override
	public void execUpdate(Bson filter, Bson update, SmofUpdateOptions options) {
		options.setReturnDocument(ReturnDocument.AFTER);
		final BsonDocument result = collection.findOneAndUpdate(filter, update, options.toFindOneAndUpdateOptions());
		final T element = parser.fromBson(result, type);
		cache.put(result.getObjectId(Element.ID).getValue(), element);
	}

	@Override
	public SmofUpdate<T> update() {
		return new SmofUpdate<>(this);
	}

	@Override
	public void replace(T element, SmofUpdateOptions options) {
		if(options.isBypassCache() || !cache.asMap().containsValue(element)) {
			final BsonDocument document = parser.toBson(element);
			final Bson query = createUniquenessQuery(document);
			options.setReturnDocument(ReturnDocument.AFTER);
			document.remove(Element.ID);
			final BsonDocument result = collection.findOneAndReplace(query, document, options.toFindOneAndReplace());
			element.setId(result.get(Element.ID).asObjectId().getValue());
			cache.put(element.getId(), element);
		}
	}

	private Bson createUniquenessQuery(BsonDocument element) {
		final List<Bson> filters = Lists.newArrayList();
		filters.add(Filters.eq(Element.ID, element.get(Element.ID)));
		indexes.stream()
		.filter(i -> i.getOptions().isUnique())
		.map(i -> createFilterFromIndex(i, element))
		.forEach(filters::add);

		return Filters.or(filters);
	}

	private Bson createFilterFromIndex(InternalIndex index, BsonDocument element) {
		final List<Bson> filters = Lists.newArrayList();
		final BsonDocument indexDoc = index.getIndex().toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());

		for(String key : indexDoc.keySet()) {
			filters.add(Filters.eq(key, element.get(key)));
		}
		return Filters.and(filters);
	}

	@Override
	public String getCollectionName() {
		return name;
	}

	@Override
	public MongoCollection<BsonDocument> getMongoCollection() {
		return collection;
	}

	@Override
	public SmofParser getParser() {
		return parser;
	}

	@Override
	public Class<T> getType() {
		return type;
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

}
