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
/*
 *******************************************************************************
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
 *****************************************************************************
 */
package org.smof.collection;

import java.util.Map;

import org.smof.element.Element;
import org.smof.exception.NoSuchCollection;
import org.smof.exception.SmofException;

import com.google.common.collect.Maps;
import com.mongodb.client.gridfs.GridFSBucket;

@SuppressWarnings("javadoc")
public class CollectionsPoolImpl implements CollectionsPool {
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private final Map<Class<? extends Element>, SmofCollection<? extends Element>> collectionsByType;
	private final Map<String, SmofCollection<? extends Element>> collectionsByName;
	private final Map<String, GridFSBucket> fsBuckets;
	
	public CollectionsPoolImpl() {
		collectionsByType = Maps.newHashMap();
		collectionsByName = Maps.newHashMap();
		fsBuckets = Maps.newHashMap();
	}
	
	@Override
    public void put(String bucketName, GridFSBucket bucket) {
		fsBuckets.put(bucketName, bucket);
	}
	
	@Override
    public <T extends Element> void put(Class<T> elClass, SmofCollection<T> collection) {
		collectionsByType.put(elClass, collection);
		collectionsByName.put(collection.getCollectionName(), collection);
	}
	
	@Override
    @SuppressWarnings("unchecked")
	public <T extends Element> SmofCollection<T> getCollection(Class<T> elClass) {
		final Class<? extends Element> validSuperType = getValidSuperType(elClass);
		if(validSuperType == null) {
			handleError(new NoSuchCollection(elClass));
			return null;
		}
		return (SmofCollection<T>) collectionsByType.get(validSuperType);
	}
	
	@Override
	public SmofCollection<? extends Element> getCollection(String name) {
		return collectionsByName.get(name);
	}

	@Override
    public GridFSBucket getBucket(String bucketName) {
		return fsBuckets.get(bucketName);
	}
	
	@Override
    public Iterable<String> getAllBuckets() {
		return fsBuckets.keySet();
	}
	
	@Override
    public void dropBucket(String bucketName) {
		fsBuckets.remove(bucketName);
	}
	
	@Override
    public void dropAllBuckets() {
		fsBuckets.values().forEach(GridFSBucket::drop);
		fsBuckets.clear();
	}

	private Class<? extends Element> getValidSuperType(Class<? extends Element> elClass) {
		if(elClass == null) {
			return null;
		}
		for(Class<? extends Element> type : collectionsByType.keySet()) {
			if(type.isAssignableFrom(elClass)) {
				return type;
			}
		}
		return null;
	}

	@Override
    public boolean dropCollection(Class<? extends Element> type) {
		final SmofCollection<? extends Element> collection = collectionsByType.remove(type);
		if(collection != null) {
			collectionsByName.remove(collection.getCollectionName());
		}
		return collection != null;
	}
	
	@Override
	public boolean dropCollection(String collectionName) {
		final SmofCollection<? extends Element> collection = collectionsByName.remove(collectionName);
		if(collection != null) {
			collectionsByType.remove(collection.getType());
		}
		return collection != null;
	}
	
	@Override
    public void dropAllCollections() {
		collectionsByType.values().forEach(SmofCollection::drop);
		collectionsByType.clear();
		collectionsByName.clear();
	}

	@Override
	public boolean contains(Class<? extends Element> type) {
		return collectionsByType.containsKey(type);
	}

	@Override
	public boolean contains(String name) {
		return collectionsByName.containsKey(name);
	}

}
