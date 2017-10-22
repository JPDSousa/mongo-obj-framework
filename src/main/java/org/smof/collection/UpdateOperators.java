/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.collection;

@SuppressWarnings("javadoc")
public enum UpdateOperators {
	
	// numbers
	INCREASE("inc"),
	MULTIPLY("mul"),
	MINIMUM("min"),
	MAXIMUM("max"),
	// registers
	SET_ON_INSERT("setOnInsert"),
	SET("set"),
	UNSET("unset"),
	// date
	CURRENT_DATE("currentDate"),
	// arrays
	ADD_TO_SET("addToSet"),
	POP("pop"),
	PULL("pull"),
	PULL_ALL("pullAll"),
	PUSH("push"),
	// modifiers
	EACH("each"),
	POSITION("position"),
	SLICE("slice"),
	SORT("sort");
	
	private final String operator;
	
	UpdateOperators(String operator) {
		this.operator = "$" + operator;
	}

	public String getOperator() {
		return operator;
	}
	
}
