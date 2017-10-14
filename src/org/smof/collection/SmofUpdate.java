package org.smof.collection;

import org.bson.BsonValue;
import org.smof.element.Element;

public interface SmofUpdate<T extends Element> {

    SmofUpdate<T> setUpsert(boolean upsert);

    SmofUpdateQuery<T> where();

    void fromElement(T element);

    SmofUpdate<T> increase(Number value, String fieldName);

    SmofUpdate<T> multiply(Number value, String fieldName);

    SmofUpdate<T> rename(String newName, String fieldName);

    SmofUpdate<T> set(BsonValue bson, String fieldName);

    SmofUpdate<T> set(Object obj, String fieldName);
}
