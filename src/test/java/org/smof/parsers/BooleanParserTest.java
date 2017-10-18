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
package org.smof.parsers;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDate;

/**
 * Created by thales minussi on 28/09/17.
 * https://github.com/tminussi
 */
@SuppressWarnings("javadoc")
public class BooleanParserTest {

	private static final BsonValue BSON_TRUE = BsonBoolean.TRUE;
	private static final BsonValue BSON_FALSE = BsonBoolean.FALSE;
	private static BooleanParser parser;

	@Before
	public void setUp() {
		parser = (BooleanParser) new SmofParser(null)
				.getParsers()
				.get(SmofType.BOOLEAN);
	}
	
	private void testSupport(Object valueTrue, Object valueFalse, Class<?> type) {
		// valid type
		assertTrue(parser.isValidType(type));
		// valid bson
		// valid serialization
		assertEquals(BSON_TRUE, parser.toBson(valueTrue, null));
		assertEquals(BSON_FALSE, parser.toBson(valueFalse, null));
		// valid deserialization
		assertEquals(valueTrue, parser.fromBson(BSON_TRUE, type, null));
		assertEquals(valueFalse, parser.fromBson(BSON_FALSE, type, null));
	}

	@Test
	public void booleanSupport() {
		testSupport(Boolean.TRUE, Boolean.FALSE, Boolean.class);
		testSupport(true, false, boolean.class);
	}
	
	@Test
	public void stringSupport() {
		final String valueTrue = Boolean.TRUE.toString();
		final String valueFalse = Boolean.FALSE.toString();
		testSupport(valueTrue, valueFalse, String.class);
	}
	
	@Test
	public void integerSupport() {
		final int valueTrue = 1;
		final int valueFalse = 0;
		testSupport(valueTrue, valueFalse, Integer.class);
		testSupport(valueTrue, valueFalse, int.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromBson_ShouldRaise_IllegalArgumentException_IfValueIsNull() {
		parser.fromBson(null, Boolean.class, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fromBson_ShouldRaise_IllegalArgumentException_IfTypeIsNull() {
		parser.fromBson(BSON_TRUE, null, null);
	}

	@Test
	public void serializeToBson_ShouldSerialize_SmofFieldCorrectly() {
		BsonValue bsonValue = parser.serializeToBson(Boolean.TRUE, null);
		assertTrue(bsonValue.isBoolean());
	}

	@Test
	public void serializeToBson_ShouldReturn_Null() {
		BsonValue bsonValue = parser.serializeToBson(LocalDate.now(), null);
		assertFalse(bsonValue.isBoolean());
	}

	@Test(expected = RuntimeException.class)
	public void serializeToBson_ShouldRaise_RuntimeException_IfValueIsNull() {
		parser.serializeToBson(null, null);
	}

	@Test
	public void isValidBson_ShouldReturn_True() {
		assertTrue(parser.isValidBson(BSON_FALSE));
		assertTrue(parser.isValidBson(BSON_TRUE));
	}

	@Test
	public void isValidBson_ShouldReturn_False() {
		boolean validBson = parser.isValidBson(new BsonArray());
		assertFalse(validBson);
	}
}
