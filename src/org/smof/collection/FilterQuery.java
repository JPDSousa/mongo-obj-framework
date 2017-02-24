package org.smof.collection;

import org.bson.conversions.Bson;

interface FilterQuery<T> {

	T withField(String fieldName, Object filterValue);
	
	T applyBsonFilter(Bson filter);
}
