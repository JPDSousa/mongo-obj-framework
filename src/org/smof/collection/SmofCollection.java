package org.smof.collection;

import java.util.Set;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.smof.element.Element;


@SuppressWarnings("javadoc")
public interface SmofCollection<T extends Element> {
	
	boolean insert(T element);

	T lookup(ObjectId id);

	Set<T> lookupAll(Iterable<T> ids);

	void update(T element);

	T get(T element);

	Stream<T> getAll();
	
}
