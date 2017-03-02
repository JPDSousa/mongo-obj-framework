package org.smof.element;

import org.bson.types.ObjectId;

@SuppressWarnings("javadoc")
public interface Element {

	static final String ID = "_id";

	ObjectId getId();
	
	String getIdAsString();

	void setId(final ObjectId id);
}
