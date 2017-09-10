package org.smof.collection;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.smof.element.Element;

@SuppressWarnings("javadoc")
public class SmofInsertResult {
	
	private boolean success;
	private Stack<Pair<String, Element>> postInserts;
	
	public SmofInsertResult() {
		super();
		this.success = false;
		this.postInserts = new Stack<>();
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Stack<Pair<String, Element>> getPostInserts() {
		return postInserts;
	}

	public void setPostInserts(Stack<Pair<String, Element>> postInserts) {
		if(postInserts != null) {
			this.postInserts = postInserts;
		}
	}
	
}
