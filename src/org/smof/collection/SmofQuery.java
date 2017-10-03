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

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.google.common.cache.Cache;
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
		final BsonDocument filterDoc = filter.toBsonDocument(BsonDocument.class, getParser().getRegistry());
		for(Map.Entry<String, BsonValue> entry : filterDoc.entrySet()) {
			getFilter().append(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public T byElement(T element) {
		return withFieldEquals(Element.ID, element.getId())
		.results()
		.first();
	}
	
}
