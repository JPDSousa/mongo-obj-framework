package org.smof.collection;

import java.util.Queue;

import org.bson.types.ObjectId;
import org.smof.element.Element;
import com.google.common.collect.EvictingQueue;

class ElementCache<T extends Element> {

	private final Queue<ObjectId> queue;
	
	ElementCache(int size) {
		queue = EvictingQueue.create(size);
	}
	
	void offer(T element) {
		queue.offer(element.getId());
	}
	
	boolean contains(T element) {
		return queue.contains(element.getId());
	}
}
