package org.smof.element;


@SuppressWarnings("javadoc")
public interface Element {
	
	public static final String ID = "_id";

	public String getID();

	public void setID(final String id) throws InvalidIdException;
	
	public static void validateStringID(final String id) throws InvalidIdException{
		if(id == null || id.isEmpty()) {
			throw new InvalidIdException(id);
		}
	}

}
