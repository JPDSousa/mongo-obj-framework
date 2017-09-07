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
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.mongodb.client.MongoCollection;


@SuppressWarnings("javadoc")
public interface SmofCollection<T extends Element> {
	
	/**
	 * Returns the mongoDB collection name
	 * 
	 * @return collection name
	 */
	String getCollectionName();
	
	/**
	 * Returns the original mongoDB collection associated with this
	 * collection.
	 * 
	 * <p>Using this method requires importing the mongoDB-Java-driver
	 * 
	 * @return mongoDB collection
	 */
	MongoCollection<BsonDocument> getMongoCollection();
	
	/**
	 * Inserts {@code element} into the collection. The element must not violate
	 * any unique index constraints
	 * 
	 * @param element element to insert
	 */
	boolean insert(T element, SmofOpOptions options);

	void execUpdate(Bson filter, Bson updates, SmofOpOptions options);
	SmofUpdate<T> update();

	SmofQuery<T> query();
	T findById(ObjectId id);
	
	SmofParser getParser();
	Class<T> getType();
	
}
