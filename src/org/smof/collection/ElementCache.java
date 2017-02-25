package org.smof.collection;

import java.util.Queue;

import org.bson.types.ObjectId;
import org.smof.element.Element;
import com.google.common.collect.EvictingQueue;

class ElementCache {

	private final Queue<ObjectId> queue;
	
	ElementCache(int size) {
		queue = EvictingQueue.create(size);
	}
	
	void offer(Element element) {
		queue.offer(element.getId());
	}
	
	boolean contains(Element element) {
		return queue.contains(element.getId());
	}
}
