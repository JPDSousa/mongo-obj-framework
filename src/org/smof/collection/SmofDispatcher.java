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

	public <T extends Element> void insert(T element) {
		final SmofUpdateOptions options = SmofUpdateOptions.create();
		options.upsert(true);
		options.bypassCache(true);
		insert(element, options);
	}
	
	public <T extends Element> void insertChild(T element) {
		final SmofUpdateOptions options = SmofUpdateOptions.create();
		options.upsert(true);
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
}
