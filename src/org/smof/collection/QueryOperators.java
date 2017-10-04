package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.BsonValue;

enum QueryOperators {
	
	//comparison operators
	EQUALS("eq"),
	NOT_EQUALS("ne"),
	GREATER("gt"),
	GREATER_OR_EQUAL("gte"),
	SMALLER("lt"),
	SMALLER_OR_EQUAL("lte"),
	IN("in"),
	NOT_IN("nin"),
	//logical operators
	AND("and"),
	OR("or"),
	NOR("nor"),
	NOT("not"),
	//evaluation operators
	MOD("mod"),
	// array operators
	ALL("all");
	
	private final String fieldName;

	private QueryOperators(String fieldName) {
		this.fieldName = fieldName;
	}
	
	String getFieldName() {
		return "$" + fieldName;
	}
	
	BsonDocument query(BsonValue value) {
		final BsonDocument doc = new BsonDocument();
		query(value, doc);
		return doc;
	}
	
	void query(BsonValue value, BsonDocument query) {
		query.append(getFieldName(), value);
	}

}
