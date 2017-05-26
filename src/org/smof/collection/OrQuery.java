/*******************************************************************************
 * Copyright (C) 2017 Joao
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

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.smof.element.Element;

import com.mongodb.client.model.Filters;

@SuppressWarnings("javadoc")
public class OrQuery<T extends Element> extends AbstractSmofQuery<T, OrQuery<T>> {

	private final SmofQuery<T> parent;
	private final List<Bson> filters;

	OrQuery(SmofQuery<T> parent) {
		super(parent.getParser(), parent.getElementClass());
		this.parent = parent;
		filters = new ArrayList<>();
	}
	
	public OrQuery<T> or(Bson filter) {
		filters.add(filter);
		return this;
	}
	
	@Override
	public OrQuery<T> applyBsonFilter(Bson filter) {
		return or(filter);
	}
	
	public SmofQuery<T> endOr() {
		parent.applyBsonFilter(Filters.or(filters));
		return parent;
	}

	@Override
	public SmofResults<T> results() {
		return endOr().results();
	}

}
