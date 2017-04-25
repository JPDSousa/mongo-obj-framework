package org.smof.collection;

import java.util.function.Predicate;

import org.smof.element.Element;

@SuppressWarnings("javadoc")
public interface CollectionOptions<E extends Element> {
	
	static <E extends Element> CollectionOptions<E> create() {
		return new CollectionOptionsImpl<>();
	}
	
	void addConstraint(Predicate<E> constraint);
	
	Iterable<Predicate<E>> getConstraints();
	
	boolean isValid(E element);

}
