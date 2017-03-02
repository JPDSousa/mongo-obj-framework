package org.smof.collection;

import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.exception.NoSuchCollection;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public class SmofDispatcher {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final CollectionsPool collections;

	public SmofDispatcher(CollectionsPool collections) {
		this.collections = collections;
	}

	@SuppressWarnings("unchecked")
	public <T extends Element> void insert(T element) {
		Class<? extends Element> type = getValidCollectionType(element.getClass());
		((SmofCollection<T>) collections.getCollection(type)).insert(element);
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
}
