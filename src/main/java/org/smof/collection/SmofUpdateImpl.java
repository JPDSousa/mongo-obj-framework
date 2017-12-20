/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
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
import static org.smof.parsers.SmofType.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.smof.element.Element;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;
import org.smof.parsers.SmofType;

import com.google.common.base.Preconditions;

class SmofUpdateImpl<T extends Element> implements SmofUpdate<T>{
	
	private final SmofCollection<T> collection;
	private final Map<String, PrimaryField> fields;
	private final SmofOpOptions options;
	private final SmofParser parser;
	private final Class<T> type;
	private BsonDocument update;
	
	SmofUpdateImpl(SmofCollection<T> collection) {
		update = new BsonDocument();
		this.parser = collection.getParser();
		this.type = collection.getType();
		this.collection = collection;
		fields = parser.getTypeStructure(type).getAllFields();
		options = new SmofOpOptionsImpl();
		options.bypassCache(true);
	}
	
	@Override
	public SmofUpdate<T> addToSet(String fieldName, Collection<?> values) {
		final SmofField field = validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = parser.toBson(values, field);
		putOrAppend(ADD_TO_SET, fieldName, bsonValue);
		return this;
	}
	
	@Override
	public SmofUpdate<T> addToSet(String fieldName, Object value) {
		final PrimaryField field = validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = parser.toBson(value, field.getSecondaryField());
		putOrAppend(ADD_TO_SET, fieldName, bsonValue);
		return this;
	}
	
	@Override
	public SmofUpdate<T> currentDate(String fieldName) {
		validateFieldName(fieldName, DATETIME);
		putOrAppend(CURRENT_DATE, fieldName, new BsonBoolean(true));
		return this;
	}

	@Override
	public SmofUpdate<T> decrease(String fieldName, Number value) {
		return increase(fieldName, symmetric(value));
	}
	
	private Number symmetric(Number value) {
		final Class<?> wrapper = ClassUtils.primitiveToWrapper(value.getClass());
		if(Integer.class.equals(wrapper)) {
			return -value.intValue();
		}
		else if(Short.class.equals(wrapper)) {
			return -value.shortValue();
		}
		else if(Float.class.equals(wrapper)) {
			return -value.floatValue();
		}
		else if(Double.class.equals(wrapper)) {
			return -value.doubleValue();
		}
		else if(Long.class.equals(wrapper)) {
			return -value.longValue();
		}
		else {
			throw new NumberFormatException(value + "is not in a valid format");
		}
	}
	
	@Override
	public SmofUpdate<T> divide(String fieldName, Number value) {
		return multiply(fieldName, inverse(value));
	}
	
	private Number inverse(Number value) {
		final Class<?> wrapper = ClassUtils.primitiveToWrapper(value.getClass());
		if(Integer.class.equals(wrapper)) {
			return 1.0/value.intValue();
		}
		else if(Short.class.equals(wrapper)) {
			return 1.0/value.shortValue();
		}
		else if(Float.class.equals(wrapper)) {
			return 1/value.floatValue();
		}
		else if(Double.class.equals(wrapper)) {
			return 1/value.doubleValue();
		}
		else if(Long.class.equals(wrapper)) {
			return 1.0/value.longValue();
		}
		else {
			throw new NumberFormatException(value + "is not in a valid format");
		}
	}
	
	@Override
	public SmofUpdate<T> increase(String fieldName, Number value) {
		final SmofField field = validateFieldName(fieldName, NUMBER);
		final BsonValue number = parser.toBson(value, field);
		putOrAppend(INCREASE, fieldName, number);
		return this;
	}
	
	@Override
	public SmofUpdate<T> maximum(String fieldName, Number value) {
		final SmofField field = validateFieldName(fieldName, NUMBER);
		final BsonValue bsonValue = parser.toBson(fieldName, field);
		putOrAppend(MAXIMUM, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> minimum(String fieldName, Number value) {
		final SmofField field = validateFieldName(fieldName, NUMBER);
		final BsonValue bsonValue = parser.toBson(fieldName, field);
		putOrAppend(MINIMUM, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> multiply(String fieldName, Number value) {
		final SmofField field = validateFieldName(fieldName, NUMBER);
		final BsonValue number = parser.toBson(value, field);
		putOrAppend(MULTIPLY, fieldName, number);
		return this;
	}

	@Override
	public SmofUpdate<T> pop(String fieldName, boolean removeFirst) {
		validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = new BsonInt32(removeFirst ? -1 : 1);
		putOrAppend(POP, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> pull(String fieldName, Object value) {
		final PrimaryField field = validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = parser.toBson(value, field.getSecondaryField());
		putOrAppend(PULL, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> pullAll(String fieldName, Collection<?> values) {
		final SmofField field = validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = parser.toBson(values, field);
		putOrAppend(PULL_ALL, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> push(String fieldName, int index, Object value) {
		return pushAll(fieldName, index, Arrays.asList(value));
	}

	@Override
	public SmofUpdate<T> push(String fieldName, Object value) {
		final PrimaryField field = validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = parser.toBson(value, field.getSecondaryField());
		putOrAppend(PUSH, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> pushAll(String fieldName, Collection<?> values) {
		final SmofField field = validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = new BsonDocument(EACH.getOperator(), parser.toBson(values, field));
		putOrAppend(PUSH, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> pushAll(String fieldName, int index, Collection<?> values) {
		final SmofField field = validateFieldName(fieldName, ARRAY);
		final BsonValue bsonValue = parser.toBson(values, field);
		final BsonDocument bsonDocument = new BsonDocument(EACH.getOperator(), bsonValue);
		bsonDocument.append(POSITION.getOperator(), new BsonInt32(index));
		putOrAppend(PUSH, fieldName, bsonDocument);
		return this;
	}

	@Override
	public SmofUpdate<T> set(String fieldName, Object value) {
		final SmofField field = validateFieldName(fieldName, null);
		final BsonValue bsonValue = parser.toBson(value, field);
		putOrAppend(SET, fieldName, bsonValue);
		return this;
	}

	@Override
	public SmofUpdate<T> setUpsert(boolean upsert) {
		options.upsert(upsert);
		return this;
	}

	@Override
	public SmofUpdate<T> unset(String fieldName) {
		validateFieldName(fieldName, null);
		putOrAppend(UNSET, fieldName, new BsonString(""));
		return this;
	}

	@Override
	public SmofUpdateQuery<T> where() {
		return new SmofUpdateQuery<>(update, collection, options, fields);
	}

	private void putOrAppend(String key, BsonDocument doc) {
		if(update.containsKey(key)) {
			update.getDocument(key).putAll(doc);
		}
		else {
			update.put(key, doc);
		}
	}
	
	private void putOrAppend(UpdateOperators op, String fieldName, BsonValue value) {
		Preconditions.checkArgument(!value.isNull(), "Cannot update a value with NULL, use $unset instead");
		putOrAppend(op.getOperator(), new BsonDocument(fieldName, value));
	}

	private PrimaryField validateFieldName(String fieldName, SmofType expectedType) {
		final PrimaryField field = fields.get(fieldName);
		Preconditions.checkArgument(field != null, fieldName + " is not a valid field name for type " + type.getName());
		Preconditions.checkArgument(expectedType == null || expectedType == field.getType(), fieldName + " is not of type: " + expectedType);
		return field;
	}

	@Override
	public String toString() {
		return update.toJson();
	}
}
