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
import java.util.regex.Pattern;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.smof.element.Element;
import org.smof.field.PrimaryField;
import org.smof.parsers.SmofParser;
import org.smof.parsers.SmofType;

import static org.smof.collection.QueryOperators.*;

@SuppressWarnings("javadoc")
public abstract class AbstractSmofQuery<T extends Element, Query extends FilterQuery<T, ?>> implements FilterQuery<T, Query> {

	private final BsonDocument query;
	private final SmofParser parser;
	private final Class<T> elementClass;
	
	protected AbstractSmofQuery(SmofParser parser, Class<T> elementClass) {
		this.query = new BsonDocument();
		this.elementClass = elementClass;
		this.parser = parser;
	}
	
	@Override
	public Class<T> getElementClass() {
		return elementClass;
	}
	
	protected BsonDocument getFilter() {
		return query;
	}

	protected SmofParser getParser() {
		return parser;
	}
	
	private Query applyQuery(String fieldName, Object value, QueryOperators op) {
		final BsonValue bsonValue = toBsonQueryValue(fieldName, value);
		return applyBsonQuery(fieldName, bsonValue, op);
	}
	
	@SuppressWarnings("unchecked")
	private Query applyBsonQuery(String fieldName, BsonValue value, QueryOperators op) {
		query.append(fieldName, op.query(value));
		return (Query) this;
	}
	
	private BsonValue toBsonQueryValue(String fieldName, Object value) {
		return toBsonQueryValue(parser.getField(elementClass, fieldName), value);
	}
	
	private BsonValue toBsonQueryValue(PrimaryField field, Object value) {
		if((field.getType() == SmofType.OBJECT && value instanceof Map)
				|| field.getType() == SmofType.ARRAY) {
			return parser.toBson(value, field.getSecondaryField());
		}
		return parser.toBson(value, field);
	}
	
	private BsonValue toBsonQueryValues(PrimaryField field, Object[] values) {
		final BsonArray bsonValue = new BsonArray();
		for (Object value : values) {
			bsonValue.add(toBsonQueryValue(field, value));
		}
		return bsonValue;
	}

	@Override
	public Query withFieldEquals(String fieldName, Object value) {
		return applyQuery(fieldName, value, EQUALS);
	}

	@Override
	public Query withFieldNotEquals(String fieldName, Object value) {
		return applyQuery(fieldName, value, NOT_EQUALS);
	}

	@Override
	public Query withFieldIn(String fieldName, Object[] values) {
		final PrimaryField field = parser.getField(elementClass, fieldName);
		final BsonValue bsonValue = toBsonQueryValues(field, values);
		return applyBsonQuery(fieldName, bsonValue, IN);
	}

	@Override
	public Query withFieldNotIn(String fieldName, Object[] values) {
		final PrimaryField field = parser.getField(elementClass, fieldName);
		final BsonValue bsonValue = toBsonQueryValues(field, values);
		return applyBsonQuery(fieldName, bsonValue, NOT_IN);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query withFieldRegex(String fieldName, Pattern value) {
		final BsonValue bsonValue = parser.toBson(value, Pattern.class);
		query.append(fieldName, bsonValue);
		return (Query) this;
	}

	@Override
	public Query withFieldGreater(String fieldName, Number value) {
		return withFieldGreater(fieldName, value, false);
	}

	@Override
	public Query withFieldGreater(String fieldName, Number value, boolean greaterOrEqual) {
		return applyQuery(fieldName, value, greaterOrEqual ? GREATER_OR_EQUAL : GREATER);
	}

	@Override
	public Query withFieldGreaterOrEqual(String fieldName, Number value) { 
		return withFieldGreater(fieldName, value, true);
	}

	@Override
	public Query withFieldSmaller(String fieldName, Number value) {
		return withFieldSmaller(fieldName, value, false);
	}

	@Override
	public Query withFieldSmaller(String fieldName, Number value, boolean smallerOrEqual) {
		return applyQuery(fieldName, value, smallerOrEqual ? SMALLER_OR_EQUAL : SMALLER);
	}

	@Override
	public Query withFieldSmallerOrEqual(String fieldName, Number value) {
		return withFieldSmaller(fieldName, value, true);
	}
}
