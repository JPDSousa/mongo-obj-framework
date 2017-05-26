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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.parsers.SmofParser;

import com.google.common.cache.Cache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

@SuppressWarnings("javadoc")
public class SmofResults<T extends Element> {
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private final Bson filter;
	private final FindIterable<BsonDocument> results;
	private final SmofParser parser;
	private final Class<T> elClass;
	private final Cache<ObjectId, T> cache;
	private final MongoCollection<BsonDocument> collection;

	SmofResults(SmofParser parser, Class<T> elementClass, Cache<ObjectId, T> cache, MongoCollection<BsonDocument> collection, Bson filter) {
		this.parser = parser;
		elClass = elementClass;
		this.cache = cache;
		this.collection = collection;
		this.filter = filter;
		results = filter == null ? collection.find() : collection.find(filter);
	}
	
	public Stream<T> stream() {
		return StreamSupport.stream(results.spliterator(), false)
				.map(d -> parse(d))
				.filter(d -> d != null);
	}

	private T parse(BsonDocument d) {
		try {
			if(d != null) {
				final ObjectId id = d.get(Element.ID).asObjectId().getValue();
				return cache.get(id, new DocumentParser(d));	
			}
		} catch (ExecutionException e) {
			handleError(e);
		}
		return null;
	}
	
	public List<T> asList() {
		return stream().collect(Collectors.toList());
	}

	public T first() {
		final BsonDocument result = results.first();
		if(result == null) {
			return null;
		}
		return parse(result);
	}
	
	public long count() {
		return filter == null ? collection.count() : collection.count(filter);
	}

	private class DocumentParser implements Callable<T>{

		private final BsonDocument document;
		
		private DocumentParser(BsonDocument document) {
			super();
			this.document = document;
		}

		@Override
		public T call() throws Exception {
			return parser.fromBson(document, elClass);
		}
	}
	
}
