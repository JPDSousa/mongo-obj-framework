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

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.smof.element.Element;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public abstract class AbstractSmofQuery<T extends Element, Query extends FilterQuery<T, ?>> implements FilterQuery<T, Query> {

	private final BsonDocument filter;
	private final SmofParser parser;
	private final Class<T> elementClass;
	
	protected AbstractSmofQuery(SmofParser parser, Class<T> elementClass) {
		this.filter = new BsonDocument();
		this.elementClass = elementClass;
		this.parser = parser;
	}
	
	@Override
	public Class<T> getElementClass() {
		return elementClass;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query withField(String fieldName, Object filterValue) {
		final SmofField field = parser.getField(elementClass, fieldName);
		final BsonValue bsonValue = parser.toBson(filterValue, field);
		filter.append(fieldName, bsonValue);
		return (Query) this;
	}

	protected BsonDocument getFilter() {
		return filter;
	}

	protected SmofParser getParser() {
		return parser;
	}
}
