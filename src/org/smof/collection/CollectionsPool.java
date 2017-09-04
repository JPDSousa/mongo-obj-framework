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

import java.util.Iterator;
import java.util.Map;

import org.smof.element.Element;

import com.google.common.collect.Maps;
import com.mongodb.client.gridfs.GridFSBucket;

@SuppressWarnings("javadoc")
public class CollectionsPool implements Iterable<SmofCollection<?>>{
	
	private final Map<Class<? extends Element>, SmofCollection<? extends Element>> collections;
	private final Map<String, GridFSBucket> fsBuckets;
	
	public CollectionsPool() {
		collections = Maps.newLinkedHashMap();
		fsBuckets = Maps.newLinkedHashMap();
	}
	
	public void put(String bucketName, GridFSBucket bucket) {
		fsBuckets.put(bucketName, bucket);
	}
	
	public <T extends Element> void put(Class<T> elClass, SmofCollection<T> collection) {
		collections.put(elClass, collection);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Element> SmofCollection<T> getCollection(Class<T> elClass) {
		return (SmofCollection<T>) collections.get(elClass);
	}
	
	public GridFSBucket getBucket(String bucketName) {
		return fsBuckets.get(bucketName);
	}
	
	public Iterable<String> getAllBuckets() {
		return fsBuckets.keySet();
	}
	
	public void removeBucket(String bucketName) {
		fsBuckets.remove(bucketName);
	}
	
	public void clearBuckets() {
		fsBuckets.clear();
	}

	@Override
	public Iterator<SmofCollection<?>> iterator() {
		return collections.values().iterator();
	}

	public Class<? extends Element> getValidSuperType(Class<? extends Element> elClass) {
		for(Class<? extends Element> type : collections.keySet()) {
			if(type.isAssignableFrom(elClass)) {
				return type;
			}
		}
		return null;
	}

	public void remove(SmofCollection<?> collection) {
		collections.remove(collection.getType());
	}

}
