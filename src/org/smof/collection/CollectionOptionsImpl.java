package org.smof.collection;

import java.util.List;
import java.util.function.Predicate;

import org.smof.element.Element;

import com.google.common.collect.Lists;

class CollectionOptionsImpl<E extends Element> implements CollectionOptions<E> {

	private final List<Predicate<E>> constraints;
	
	CollectionOptionsImpl() {
		constraints = Lists.newArrayList();
	}

	@Override
	public void addConstraint(Predicate<E> constraint) {
		constraints.add(constraint);
	}

	@Override
	public Iterable<Predicate<E>> getConstraints() {
		return constraints;
	}

	@Override
	public boolean isValid(E element) {
		return constraints.stream()
				.allMatch(p -> p.test(element));
	}
	
	
}
