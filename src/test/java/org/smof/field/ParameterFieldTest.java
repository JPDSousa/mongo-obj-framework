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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.Before;
import org.junit.Test;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;
import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class ParameterFieldTest {

	private static ParameterField guineaPig;
	private static PrimaryField primary;
	@SmofString(name = "paramA")
	private static String paramA;
	
	@SuppressWarnings("unused")
	private static void guineaPig(@SmofParam(name = "paramA") String paramA) {
		//dummy
	}
	
	@Before
	public final void setUp() throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		final Method method = ParameterFieldTest.class.getDeclaredMethod("guineaPig", String.class);
		final Parameter param = method.getParameters()[0];
		final Field field = ParameterFieldTest.class.getDeclaredField("paramA");
		primary = new PrimaryField(field, SmofType.STRING);
		guineaPig = new ParameterField(param, param.getAnnotation(SmofParam.class));
		guineaPig.setPrimaryField(primary);
	}
	
	@Test
	public final void testGetType() {
		assertEquals(SmofType.STRING, guineaPig.getType());
		guineaPig.setPrimaryField(null);
		assertNull(guineaPig.getType());
	}

	@Test
	public final void testGetPrimaryField() {
		assertEquals(primary, guineaPig.getPrimaryField());
	}

	@Test
	public final void testSetPrimaryField() {
		guineaPig.setPrimaryField(null);
		assertNull(guineaPig.getPrimaryField());
	}

	@Test
	public final void testGetFieldClass() {
		assertEquals(String.class, guineaPig.getFieldClass());
	}

	@Test
	public final void testGetName() {
		assertEquals("paramA", guineaPig.getName());
	}

}
