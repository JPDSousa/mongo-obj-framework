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
/* ******************************************************************************
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

import java.io.IOException;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;

import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.gridfs.SmofGridRef;
import org.smof.gridfs.SmofGridStreamManager;
import org.smof.utils.BsonUtils;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;


@SuppressWarnings("javadoc")
public class SmofDispatcherImpl implements SmofDispatcher {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final CollectionsPool collections;
	private final SmofGridStreamManager streamManager;

	public SmofDispatcherImpl() {
		this.collections = new CollectionsPoolImpl();
		this.streamManager = SmofGridStreamManager.newStreamManager(collections);
	}
	
	@Override
    public void put(String bucketName, GridFSBucket bucket) {
		collections.put(bucketName, bucket);
	}
	
	@Override
    public <T extends Element> void put(Class<T> elClass, SmofCollection<T> collection) {
		collections.put(elClass, collection);
	}
	
	@Override
    public void dropBucket(String bucketName) {
		final GridFSBucket bucket = collections.getBucket(bucketName);
		bucket.drop();
		collections.removeBucket(bucketName);
	}

	@Override
    public void dropAllBuckets() {
		for(String bucketName : collections.getAllBuckets()) {
			collections.getBucket(bucketName).drop();
		}
		collections.clearBuckets();
	}

	@Override
    public SmofGridStreamManager getGridStreamManager() {
		return streamManager;
	}

	@Override
    public boolean dropCollection(String collectionName) {
		SmofCollection<?> toDrop = null;
		for(SmofCollection<?> collection : collections) {
			if(collectionName.equals(collection.getCollectionName())) {
				toDrop = collection;
				break;
			}
		}
		if(toDrop != null) {
			toDrop.getMongoCollection().drop();
			collections.remove(toDrop);
			return true;
		}
		return false;
	}

	@Override
    public void dropCollections() {
		for(SmofCollection<?> collection : collections) {
			collection.getMongoCollection().drop();
		}
		collections.clearCollections();
	}
	
	@Override
    public <T extends Element> SmofCollection<T> getCollection(Class<T> elClass) {
		return collections.getCollection(elClass);
	}

	@Override
    public final <T extends Element> boolean insert(T element) {
		return insert(element, new SmofOpOptionsImpl());
	}
	
	@Override
    @SuppressWarnings("unchecked")
	public <T extends Element> boolean insert(T element, SmofOpOptions options) {
        if (SmofGridRef.class.isAssignableFrom(element.getClass())) {
            return uploadFile((SmofGridRef) element);
        }

        final SmofCollection<T> collection = (SmofCollection<T>) collections.getCollection(element.getClass());
        final SmofInsertResult result = collection.insert(element, options);

        return result.isSuccess() && onInsertSuccess(element, collection, result);

    }

	private <T extends Element> boolean onInsertSuccess(T element, SmofCollection<T> collection, SmofInsertResult result) {
		final Stack<Pair<String, Element>> stack = result.getPostInserts();
		if(!stack.isEmpty()) {
			final SmofUpdate<T> update = new SmofUpdateImpl<>(collection);
			boolean updateSuccess = true;
			while(!stack.isEmpty() && updateSuccess) {
				final Pair<String, Element> current = stack.pop();
				final Element currentElement = current.getRight();
				final BsonObjectId id;
				updateSuccess = insert(currentElement);
				id = BsonUtils.toBsonObjectId(currentElement);
				update.set(id, current.getLeft());
			}
			update.where().idEq(element.getId());
			return updateSuccess;
		}
		return true;
	}

	@Override
    @SuppressWarnings("cast")
	public <T extends Element> T findById(ObjectId id, Class<T> elementClass) {
		return collections.getCollection(elementClass).findById(id);
	}
	
	@Override
    public GridFSFile loadMetadata(SmofGridRef ref) {
		return streamManager.loadFileMetadata(ref);
	}

	private boolean uploadFile(SmofGridRef ref) {
		try {
			if(ref.getAttachedByteArray() != null) {
				streamManager.uploadStream(ref);
			}
			else {
				streamManager.uploadFile(ref);
			}
			return true;
		} catch (IOException e) {
			handleError(e);
			return false;
		}
	}
	
}
