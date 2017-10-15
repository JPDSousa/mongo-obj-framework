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
package org.smof.element;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.bson.types.ObjectId;
import org.smof.annnotations.SmofObjectId;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public abstract class AbstractElement implements Element {
	
	@SmofObjectId(name = ID, ref = "")
	private ObjectId id;
	
	protected AbstractElement() {
		this(new ObjectId());
	}

	protected AbstractElement(final ObjectId initialID) {
		this(initialID, false);
	}
	
	protected AbstractElement(ObjectId id, boolean allowInitialNullId) {
		if(!allowInitialNullId && id == null) {
			throw new SmofException(new IllegalArgumentException("Id cannot be null"));
		}
		this.id = id;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public void setId(final ObjectId id) {
		if(id == null) {
			throw new SmofException(new IllegalArgumentException("Id cannot be null"));
		}
		this.id = id;
	}

	@Override
	public String getIdAsString() {
		return id.toHexString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		return result * prime;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass();
	}

	@Override
	public LocalDateTime getStorageTime() {
		return LocalDateTime.ofInstant(id.getDate().toInstant(), ZoneId.systemDefault());
	}	
	
	
}
