package org.smof.collection;

import org.bson.BsonValue;
import org.smof.element.Element;

public interface SmofUpdate<T extends Element> {

    public SmofUpdate<T> setUpsert(boolean upsert);

    public SmofUpdateQuery<T> where();

    public void fromElement(T element);

    public SmofUpdate<T> increase(Number value, String fieldName);

    public SmofUpdate<T> multiply(Number value, String fieldName);

    public SmofUpdate<T> rename(String newName, String fieldName);

    public SmofUpdate<T> set(BsonValue bson, String fieldName);

    public SmofUpdate<T> set(Object obj, String fieldName);
}
