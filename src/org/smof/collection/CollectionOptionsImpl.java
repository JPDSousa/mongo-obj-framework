package org.smof.collection;

import java.util.List;
import java.util.function.Predicate;

import org.smof.element.Element;
import org.smof.exception.SmofException;

import com.google.common.collect.Lists;

class CollectionOptionsImpl<E extends Element> implements CollectionOptions<E> {

	private final List<Predicate<E>> constraints;
	private boolean throwOnConstraintBreach;
	
	CollectionOptionsImpl() {
		constraints = Lists.newArrayList();
		throwOnConstraintBreach = true;
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
		boolean isValid = constraints.stream()
				.allMatch(p -> p.test(element));
		if(!isValid && throwOnConstraintBreach) {
			throw new SmofException(new IllegalArgumentException(element + " breaks one or more constraints."));
		}
		return isValid;
	}

	@Override
	public void throwOnConstraintBreach(boolean throu) {
		this.throwOnConstraintBreach = throu;
	}
	
	
}
