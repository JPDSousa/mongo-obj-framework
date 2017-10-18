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
package org.smof.utils;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.BsonLazyObjectId;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class BsonUtils {
	
	/** This class not support new instances.
	/* 
	/**/
	private BsonUtils() {}
	
	public static BsonObjectId toBsonObjectId(Element value) {
		final ObjectId id = value.getId();
		return new BsonObjectId(id);
	}
	
	public static Stack<Pair<String, Element>> extrackPosInsertions(BsonDocument doc) {
		final BsonArray bsonPosInsert = doc.remove(SmofParser.ON_INSERT).asArray();
		final Stack<Pair<String, Element>> posInsertions = new Stack<>();
		bsonPosInsert.stream()
			.map(v -> (BsonLazyObjectId) v)
			.map(id -> Pair.of(id.getFieldName(), id.getElement()))
			.forEach(posInsertions::push);
		return posInsertions;
	}

}
