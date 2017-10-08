package org.smof.collection;

import org.smof.element.Element;

import java.util.List;
import java.util.stream.Stream;

public interface SmofResults<T extends Element> {
    Stream stream();

    List asList();

    Element first();

    long count();
}
