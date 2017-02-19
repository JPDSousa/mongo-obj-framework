package org.smof.parsers;

import org.bson.BsonValue;
import org.smof.annnotations.SmofField;

interface BsonParser {

	BsonValue toBson(Object value, SmofField fieldOpts);
	
	<T> T fromBson(BsonValue value, Class<T> type);
	
	boolean isValidType(Class<?> type, SmofField fieldOpts);
	
	boolean isValidBson(BsonValue value);
}
