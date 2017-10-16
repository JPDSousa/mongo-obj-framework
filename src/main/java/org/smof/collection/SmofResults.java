package org.smof.collection;

import org.smof.element.Element;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("javadoc")
public interface SmofResults<T extends Element> {
	
    Stream<T> stream();

    List<T> asList();

    T first();

    long count();
}
