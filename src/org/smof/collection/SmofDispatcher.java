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

import java.io.IOException;

import org.bson.types.ObjectId;

import org.smof.element.Element;
import org.smof.exception.NoSuchCollection;
import org.smof.exception.SmofException;
import org.smof.gridfs.SmofGridRef;
import org.smof.gridfs.SmofGridStreamManager;


@SuppressWarnings("javadoc")
public class SmofDispatcher {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final CollectionsPool collections;
	private final SmofGridStreamManager streamManager;

	public SmofDispatcher(CollectionsPool collections, SmofGridStreamManager streamManager) {
		this.collections = collections;
		this.streamManager = streamManager;
	}

	public <T extends Element> void insert(T element) {
		final SmofUpdateOptions options = SmofUpdateOptions.create();
		options.bypassCache(true);
		insert(element, options);
	}
	
	public <T extends Element> void insertChild(T element) {
		final SmofUpdateOptions options = SmofUpdateOptions.create();
		insert(element, options);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Element> void insert(T element, SmofUpdateOptions options) {		
		final Class<? extends Element> type = getValidCollectionType(element.getClass());
		((SmofCollection<T>) collections.getCollection(type)).replace(element, options);
	}

	@SuppressWarnings("cast")
	public <T extends Element> T findById(ObjectId id, Class<T> elementClass) {
		getValidCollectionType(elementClass);
		return ((SmofCollection<T>) collections.getCollection(elementClass)).findById(id);
	}

	private Class<? extends Element> getValidCollectionType(Class<? extends Element> elementClass) {
		final Class<? extends Element> validSuperType = collections.getValidSuperType(elementClass);
		if(validSuperType == null) {
			handleError(new NoSuchCollection(elementClass));
			return null;
		}
		return validSuperType;
	}

	public void uploadFile(SmofGridRef fileRef) {
		try {
			streamManager.uploadFile(fileRef);
		} catch (IOException e) {
			handleError(e);
		}
	}
	
}
