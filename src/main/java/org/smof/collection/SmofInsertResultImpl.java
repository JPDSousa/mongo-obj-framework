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
