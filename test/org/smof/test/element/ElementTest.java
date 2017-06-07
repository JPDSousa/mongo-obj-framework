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
package org.smof.test.element;

import static org.junit.Assert.*;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.smof.element.AbstractElement;
import org.smof.element.Element;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public class ElementTest {

	
	@Test
	public final void testElementDotted() {
		final String dotted = Element.dotted("a", "b", "c");
		assertEquals("a.b.c", dotted);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void testNullId() throws Throwable {
		try {
			DummyElement dummyElement = new DummyElement(null);
			dummyElement.setId(null);
		} catch (SmofException e) {
			throw e.getCause();
		}
	}
	
	/**
	 * Test method for {@link org.smof.element.AbstractElement#AbstractElement()}.
	 */
	@Test
	public final void testAbstractElement() {
		assertNotNull(new DummyElement().getId());
	}

	/**
	 * Test method for {@link org.smof.element.AbstractElement#AbstractElement(org.bson.types.ObjectId)}.
	 */
	@Test
	public final void testAbstractElementObjectId() {
		final ObjectId id = new ObjectId();
		assertEquals(id, new DummyElement(id).getId());
	}

	/**
	 * Test method for {@link org.smof.element.AbstractElement#getId()} and {@link org.smof.element.AbstractElement#setId(org.bson.types.ObjectId)}.
	 */
	@Test
	public final void testGetId() {
		final ObjectId id = new ObjectId();
		final DummyElement element = new DummyElement();
		element.setId(id);
		assertEquals(id, element.getId());
	}

	/**
	 * Test method for {@link org.smof.element.AbstractElement#getIdAsString()}.
	 */
	@Test
	public final void testGetIdAsString() {
		final ObjectId id = new ObjectId();
		assertEquals(id.toHexString(), new DummyElement(id).getIdAsString());
	}

	/**
	 * Test method for {@link org.smof.element.AbstractElement#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		final ObjectId id = new ObjectId();
		final DummyElement el1 = new DummyElement();
		final DummyElement el2 = new DummyElement(id);
		final DummyElement el4 = new DummyElement(id);
		final AbstractElement el3 = new AbstractElement(id) {
			//nothing to add
		};
		assertEquals(el1, el1);
		assertNotEquals(el1, null);
		assertEquals(el4, el2);
		assertNotEquals(el1, el2);
		assertNotEquals(el1, el4);
		assertNotEquals(el1, el3);
		assertNotEquals(el3, el4);
	}
	
	private class DummyElement extends AbstractElement {
		
		private DummyElement() {
			super();
		}
		
		private DummyElement(ObjectId id) {
			super(id);
		}
	}

}
