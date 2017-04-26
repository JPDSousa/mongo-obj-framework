package org.smof.element;

import org.bson.types.ObjectId;

@SuppressWarnings("javadoc")
public interface Element {

	String ID = "_id";
	String OID = "$oid";
	
	static String dotted(String... fields) {
		return String.join(".", fields);
	}

	ObjectId getId();
	
	String getIdAsString();

	void setId(final ObjectId id);
}
