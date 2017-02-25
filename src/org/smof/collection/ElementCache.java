package org.smof.collection;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

import org.bson.types.ObjectId;
import org.smof.element.Element;

class ElementCache<T extends Element> {
	
	static <T extends Element> ElementCache<T> create(int maxSize) {
		return new ElementCache<>(maxSize);
	}

	private final Queue<T> queue;
	private final Map<ObjectId, T> map;
	private final int maxSize;
	
	private ElementCache(int maxSize) {
		queue = new ArrayDeque<>(maxSize);
		map = new LinkedHashMap<>();
		this.maxSize = maxSize;
	}
	
	boolean offer(T element) {
		checkNotNull(element);
		if(maxSize == 0 || contains(element)) {
			return true;
		}
		if(queue.size() == maxSize) {
			remove();
		}
		add(element);
		return true;
	}

	private void add(T element) {
		queue.add(element);
		map.put(element.getId(), element);
	}
	
	private void remove() {
		final T element = queue.remove();
		map.remove(element.getId());
	}

	T get(ObjectId id) {
		return map.get(id);
	}
	
	private void checkNotNull(T element) {
		if(element == null) {
			throw new IllegalArgumentException("Null values are not valid.");
		}
	}

	boolean contains(T element) {
		return map.containsKey(element.getId());
	}
}
