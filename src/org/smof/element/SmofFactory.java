package org.smof.element;

import org.bson.BsonDocument;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public interface SmofFactory<E> {

	E createSmofObject(final BsonDocument map) throws SmofException;
}
