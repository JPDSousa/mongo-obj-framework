package org.smof.parsers;

import org.bson.BsonValue;
import org.smof.field.SmofField;

interface BsonParser {

	BsonValue toBson(Object value, SmofField fieldOpts);
	
	<T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts);
	
	boolean isValidType(SmofField fieldOpts);
	
	boolean isValidType(Class<?> type);
	
	boolean isValidBson(BsonValue value);
}
