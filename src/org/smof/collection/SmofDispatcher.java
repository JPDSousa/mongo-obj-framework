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
		checkCollection(element.getClass());
		((SmofCollection<T>) collections.getCollection(element.getClass())).insert(element);
	}
	
	@SuppressWarnings("cast")
	public <T extends Element> T findById(ObjectId id, Class<T> elementClass) {
		checkCollection(elementClass);
		return ((SmofCollection<T>) collections.getCollection(elementClass)).findById(id);
	}
	
	private void checkCollection(Class<? extends Element> elementClass) {
		if(!collections.contains(elementClass)) {
			handleError(new NoSuchCollection(elementClass));
		}
	}
}
