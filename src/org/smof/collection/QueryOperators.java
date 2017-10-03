package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.BsonValue;

enum QueryOperators {
	
	EQUALS("eq"),
	NOT_EQUALS("ne"),
	GREATER("gt"),
	GREATER_OR_EQUAL("gte"),
	SMALLER("lt"),
	SMALLER_OR_EQUAL("lte"),
	IN("in"),
	NOT_IN("nin");
	
	private final String fieldName;

	private QueryOperators(String fieldName) {
		this.fieldName = fieldName;
	}
	
	BsonDocument query(BsonValue value) {
		return new BsonDocument(fieldName, value);
	}
	

}
