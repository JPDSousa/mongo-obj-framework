package org.smof.exception;

import org.bson.BsonType;

@SuppressWarnings("javadoc")
public class InvalidBsonTypeException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	public InvalidBsonTypeException(BsonType type, String fieldName) {
		super("Invalid bson type " + type.name() + " for field " + fieldName);
	}

}
