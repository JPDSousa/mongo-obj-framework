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
package org.smof.field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;
import org.smof.annnotations.SmofObject;
import org.smof.field.SecondaryField;
import org.smof.parsers.SmofType;

/**
 * @author Joao
 *
 */
public class SecondaryFieldTest {

	private static final Class<?> CLASS = String.class;
	private static final SmofType TYPE = SmofType.STRING;
	private static final String NAME = "test";
	
	private static SecondaryField guineaPig;
	
	/**
	 * Set up method
	 */
	@Before
	public void setUp() {
		final Annotation annotation = mock(SmofObject.class);
		guineaPig = new SecondaryField(NAME, TYPE, CLASS, annotation);
	}

	/**
	 * Test method for {@link org.smof.field.SecondaryField#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(TYPE, guineaPig.getType());
	}

	/**
	 * Test method for {@link org.smof.field.SecondaryField#getFieldClass()}.
	 */
	@Test
	public final void testGetFieldClass() {
		assertEquals(CLASS, guineaPig.getFieldClass());
	}

	/**
	 * Test method for {@link org.smof.field.SecondaryField#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals(NAME, guineaPig.getName());
	}

}
