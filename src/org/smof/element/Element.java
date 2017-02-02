package org.smof.element;

import org.bson.types.ObjectId;

@SuppressWarnings("javadoc")
public interface Element {
	
	public static final String ID = "_id";

	public ObjectId getId();
	
	public String getIdAsString();

	public void setId(final ObjectId id) throws InvalidIdException;
	
	public static void validateStringId(final String id) throws InvalidIdException{
		if(id == null || id.isEmpty()) {
			throw new InvalidIdException(id);
		}
	}
}
