package org.smof.collection;

import org.bson.conversions.Bson;
import org.smof.element.Element;

interface FilterQuery<E extends Element, T extends FilterQuery<E, ?>> {

	Class<E> getElementClass();
	
	T withField(String fieldName, Object filterValue);
	
	T applyBsonFilter(Bson filter);
	
	SmofResults<E> results();
}
