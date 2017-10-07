package org.smof.collection;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.gridfs.SmofGridRef;
import org.smof.gridfs.SmofGridStreamManager;

public interface SmofDispatcher {
    void put(String bucketName, GridFSBucket bucket);

    <T extends Element> void put(Class<T> elClass, SmofCollection<T> collection);

    void dropBucket(String bucketName);

    void dropAllBuckets();

    SmofGridStreamManager getGridStreamManager();

    boolean dropCollection(String collectionName);

    void dropCollections();

    <T extends Element> SmofCollection<T> getCollection(Class<T> elClass);

    <T extends Element> boolean insert(T element);

    @SuppressWarnings("unchecked")
    <T extends Element> boolean insert(T element, SmofOpOptions options);

    @SuppressWarnings("cast")
    <T extends Element> T findById(ObjectId id, Class<T> elementClass);

    GridFSFile loadMetadata(SmofGridRef ref);
}
