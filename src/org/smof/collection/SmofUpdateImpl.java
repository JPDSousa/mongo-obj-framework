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

import static org.smof.collection.UpdateOperators.*;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.smof.element.Element;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

import com.google.common.base.Preconditions;

@SuppressWarnings("javadoc")
class SmofUpdateImpl<T extends Element> implements SmofUpdate<T>{
	
	private BsonDocument update;
	private final SmofParser parser;
	private final Map<String, PrimaryField> fields;
	private final Class<T> type;
	private final SmofCollection<T> collection;
	private final SmofOpOptions options;
	
	SmofUpdateImpl(SmofCollection<T> collection) {
		update = new BsonDocument();
		this.parser = collection.getParser();
		this.type = collection.getType();
		this.collection = collection;
		fields = parser.getTypeStructure(type).getAllFields();
		options = new SmofOpOptionsImpl();
		options.bypassCache(true);
	}
	
	private SmofField validateFieldName(String fieldName) {
		final PrimaryField field = fields.get(fieldName);
		Preconditions.checkArgument(field != null, fieldName + " is not a valid field name for type " + type.getName());
		return field;
	}
	
	public SmofUpdate<T> setUpsert(boolean upsert) {
		options.upsert(upsert);
		return this;
	}
	
	public SmofUpdateQuery<T> where() {
		return new SmofUpdateQuery<>(update, collection, options, fields);
	}
	
	public void fromElement(T element) {
		collection.insert(element, options);
		parser.reset();
	}

	public SmofUpdate<T> increase(Number value, String fieldName) {
		final SmofField field = validateFieldName(fieldName);
		final BsonValue number = parser.toBson(value, field);
		final BsonDocument doc = new BsonDocument();
		doc.put(fieldName, number);
		putOrAppend(INCREASE, doc);
		return this;
	}
	
	public SmofUpdate<T> multiply(Number value, String fieldName) {
		final SmofField field = validateFieldName(fieldName);
		final BsonValue number = parser.toBson(value, field);
		final BsonDocument doc = new BsonDocument(fieldName, number);
		putOrAppend(MULTIPLY, doc);
		return this;
	}
	
	public SmofUpdate<T> rename(String newName, String fieldName) {
		validateFieldName(fieldName);
		final BsonDocument doc = new BsonDocument();
		doc.put(fieldName, new BsonString(newName));
		putOrAppend(RENAME, doc);
		return this;
	}
	
	public SmofUpdate<T> set(BsonValue bson, String fieldName) {
		final BsonDocument doc = new BsonDocument(fieldName, bson);
		putOrAppend(SET, doc);
		return this;
	}
	
	public SmofUpdate<T> set(Object obj, String fieldName) {
		final SmofField field = validateFieldName(fieldName);
		final BsonValue value = parser.toBson(obj, field);
		return set(value, fieldName);
	}
	
	private void putOrAppend(UpdateOperators op, BsonDocument doc) {
		putOrAppend(op.getOperator(), doc);
	}

	private void putOrAppend(String key, BsonDocument doc) {
		if(update.containsKey(key)) {
			update.getDocument(key).putAll(doc);
		}
		else {
			update.put(key, doc);
		}
	}
}
