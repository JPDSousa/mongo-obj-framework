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
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class SmofUpdateQuery<T extends Element> {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private final BsonDocument update;
	private final SmofCollection<T> collection;
	private BsonDocument filter;
	private final SmofOpOptions options;
	private final SmofParser parser;
	private final Map<String, PrimaryField> fields;
	private final Class<T> type;
	
	SmofUpdateQuery(BsonDocument update, SmofCollection<T> collection, SmofOpOptions options, Map<String, PrimaryField> fields) {
		super();
		this.update = update;
		this.collection = collection;
		filter = new BsonDocument();
		this.options = options;
		this.parser = collection.getParser();
		this.fields = fields;
		this.type = collection.getType();
	}
	
	private SmofField validateFieldName(String fieldName) {
		final PrimaryField field = fields.get(fieldName);
		if(field == null) {
			handleError(new IllegalArgumentException(fieldName + " is not a valid field name for type " + type.getName()));
		}
		return field;
	}
	
	public SmofUpdateQuery<T> fieldEq(String fieldName, Object value) {
		final SmofField field = validateFieldName(fieldName);
		filter.append(fieldName, parser.toBson(value, field));
		return this;
	}
	
	public void idEq(ObjectId id) {
		idEq(new BsonObjectId(id));
	}
	
	public void idEq(BsonObjectId id) {
		filter = new BsonDocument(Element.ID, id);
		execute();
	}
	
	public void execute() {
		collection.execUpdate(filter, update, options);
		parser.reset();
	}
	
}
