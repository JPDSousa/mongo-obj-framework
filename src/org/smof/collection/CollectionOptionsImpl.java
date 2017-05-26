/*******************************************************************************
 * Copyright (C) 2017 Joao
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
