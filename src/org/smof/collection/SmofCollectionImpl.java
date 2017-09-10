/*******************************************************************************
 * Copyright (C) 2017 Joao
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
import com.google.common.collect.Sets;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
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
	private final CollectionOptions<T> options;

	SmofCollectionImpl(String name, MongoCollection<BsonDocument> collection, Class<T> type, SmofParser parser, CollectionOptions<T> options) {
		this.collection = collection;
		this.name = name;
		this.parser = parser;
		this.type = type;
		this.indexes = loadDBIndexes();
		updateIndexes();
		cache = CacheBuilder.newBuilder()
				.maximumSize(CACHE_SIZE)
				.expireAfterAccess(5, TimeUnit.MINUTES)
				.build();
		this.options = options;
	}

	private Set<InternalIndex> loadDBIndexes() {
		final Set<InternalIndex> indexes = Sets.newLinkedHashSet();
		final ListIndexesIterable<BsonDocument> bsonIndexes = collection.listIndexes(BsonDocument.class);
		for(BsonDocument bsonIndex : bsonIndexes) {
			indexes.add(InternalIndex.fromBson(bsonIndex));
		}
		return indexes;
	}

	private void updateIndexes() {
		final Collection<InternalIndex> newIndexes = CollectionUtils.removeAll(parser.getIndexes(type), indexes);
		for(InternalIndex index : newIndexes) {
			collection.createIndex(index.getIndex(), index.getOptions());
			indexes.add(index);
		}
	}

	@Override
	public boolean insert(T element, SmofOpOptions options) {
		final boolean isValid = this.options.isValid(element);
		if(isValid) {
			if(this.options.isUpsert()) {
				return replace(element, options);
			}
			else if(!cache.asMap().containsKey(element.getId())) {
				return insert(element);
			}
		}
		return isValid;
	}

	private boolean insert(T element) {
		try {
			final BsonDocument document = parser.toBson(element);
			collection.insertOne(document);
			cache.put(element.getId(), element);
			return true;
		} catch(MongoWriteException e) {
			if(this.options.isThrowOnInsertDuplicate()) {
				throw e;
			}
			return false;
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
	public void execUpdate(Bson filter, Bson update, SmofOpOptions options) {
		options.setReturnDocument(ReturnDocument.AFTER);
		final BsonDocument result = collection.findOneAndUpdate(filter, update, options.toFindOneAndUpdateOptions());
		final T element = parser.fromBson(result, type);
		cache.put(result.getObjectId(Element.ID).getValue(), element);
	}

	@Override
	public SmofUpdate<T> update() {
		return new SmofUpdate<>(this);
	}

	private boolean replace(T element, SmofOpOptions options) {
		options.upsert(true);
		if(options.isBypassCache() || !cache.asMap().containsValue(element)) {
			final BsonDocument document = parser.toBson(element);
			final Bson query = createUniquenessQuery(document);
			options.setReturnDocument(ReturnDocument.AFTER);
			document.remove(Element.ID);
			final BsonDocument result = collection.findOneAndReplace(query, document, options.toFindOneAndReplace());
			element.setId(result.get(Element.ID).asObjectId().getValue());
			cache.put(element.getId(), element);
		}
		return true;
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

	@Override
	public CollectionOptions<T> getCollectionOptions() {
		return options;
	}

}
