package org.smof.collection;

enum Operators {
	
	INCREASE("inc"),
	MULTIPLY("mul"),
	RENAME("rename"),
	SET_ON_INSERT("setOnInsert"),
	SET("set"),
	UNSET("unset"),
	MINIMUM("min"),
	MAXIMUM("miax"),
	CURRENT_DATE("currentDate");
	
	private final String operator;
	
	private Operators(String operator) {
		this.operator = "$" + operator;
	}

	String getOperator() {
		return operator;
	}
	
}
