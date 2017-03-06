package org.smof.collection;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.element.Element;

@SuppressWarnings("javadoc")
public class CollectionsPool implements Iterable<SmofCollection<?>>{
	
	private final Map<Class<? extends Element>, SmofCollection<? extends Element>> collections;
	
	
	public CollectionsPool() {
		collections = new LinkedHashMap<>();
	}
	
	public <T extends Element> void put(Class<T> elClass, SmofCollection<T> collection) {
		collections.put(elClass, collection);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Element> SmofCollection<T> getCollection(Class<T> elClass) {
		return (SmofCollection<T>) collections.get(elClass);
	}

	@Override
	public Iterator<SmofCollection<?>> iterator() {
		return collections.values().iterator();
	}

	public Class<? extends Element> getValidSuperType(Class<? extends Element> elClass) {
		for(Class<? extends Element> type : collections.keySet()) {
			if(type.isAssignableFrom(elClass)) {
				return type;
			}
		}
		return null;
	}

	public void remove(SmofCollection<?> collection) {
		collections.remove(collection.getType());
	}

}
