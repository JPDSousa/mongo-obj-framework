package org.smof.collection;

import java.util.Set;
import java.util.stream.Stream;

import org.smof.element.Element;


@SuppressWarnings("javadoc")
public interface NoSQLCollection<T extends Element> {
	
	void add(T element);

	T lookup(String id);

	Set<T> lookupAll(Iterable<T> ids);

	void update(T element);

	T get(T element);

	Stream<T> getAll();
	
}
