package org.smof.collection;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.smof.element.Element;

@SuppressWarnings("javadoc")
public class SmofInsertResultImpl implements SmofInsertResult {
	
	private boolean success;
	private Stack<Pair<String, Element>> postInserts;
	
	public SmofInsertResultImpl() {
		super();
		this.success = false;
		this.postInserts = new Stack<>();
	}

	@Override
    public boolean isSuccess() {
		return success;
	}

	@Override
    public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
    public Stack<Pair<String, Element>> getPostInserts() {
		return postInserts;
	}

	@Override
    public void setPostInserts(Stack<Pair<String, Element>> postInserts) {
		if(postInserts != null) {
			this.postInserts = postInserts;
		}
	}
	
}
