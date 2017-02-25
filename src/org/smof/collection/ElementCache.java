package org.smof.collection;

import java.util.Queue;

import org.smof.element.Element;
import com.google.common.collect.EvictingQueue;

class ElementCache {

	private final Queue<Element> queue;
	
	ElementCache(int size) {
		queue = EvictingQueue.create(size);
	}
	
	void offer(Element element) {
		queue.offer(element);
	}
	
	boolean contains(Element element) {
		for (Element e : queue) {
			if(sameClass(element, e) && sameId(element, e)) {
				return true;
			}
		}
		return false;
	}

	private boolean sameId(Element element, Element e) {
		return element.getId().equals(e.getId());
	}

	private boolean sameClass(Element element, Element e) {
		return e.getClass().equals(element.getClass());
	}
	
	
}
