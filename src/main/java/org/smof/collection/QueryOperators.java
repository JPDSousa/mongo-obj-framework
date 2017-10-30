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

import org.bson.BsonDocument;
import org.bson.BsonValue;

enum QueryOperators {
	
	//comparison operators
	EQUALS("eq"),
	NOT_EQUALS("ne"),
	GREATER("gt"),
	GREATER_OR_EQUAL("gte"),
	SMALLER("lt"),
	SMALLER_OR_EQUAL("lte"),
	IN("in"),
	NOT_IN("nin"),
	//logical operators
	AND("and"),
	OR("or"),
	NOR("nor"),
	NOT("not"),
	//evaluation operators
	MOD("mod"),
	// array operators
	ALL("all");
	
	private final String fieldName;

	QueryOperators(String fieldName) {
		this.fieldName = fieldName;
	}
	
	String getFieldName() {
		return "$" + fieldName;
	}
	
	BsonDocument query(BsonValue value) {
		final BsonDocument doc = new BsonDocument();
		query(value, doc);
		return doc;
	}
	
	void query(BsonValue value, BsonDocument query) {
		query.append(getFieldName(), value);
	}

}
