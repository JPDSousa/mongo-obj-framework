package org.smof.collection;

import com.mongodb.client.gridfs.GridFSBucket;
import org.smof.element.Element;

import java.util.Iterator;

public interface CollectionsPool extends Iterable<SmofCollection<?>> {
    void put(String bucketName, GridFSBucket bucket);

    <T extends Element> void put(Class<T> elClass, SmofCollection<T> collection);

    @SuppressWarnings("unchecked")
    <T extends Element> SmofCollection<T> getCollection(Class<T> elClass);

    GridFSBucket getBucket(String bucketName);

    Iterable<String> getAllBuckets();

    void removeBucket(String bucketName);

    void clearBuckets();

    @Override
    Iterator<SmofCollection<?>> iterator();

    void remove(SmofCollection<?> collection);

    void clearCollections();
}
