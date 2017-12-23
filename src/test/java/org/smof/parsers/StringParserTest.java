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

import org.bson.*;
import org.bson.types.Decimal128;
import org.junit.Test;
import org.junit.Before;
import org.smof.dataModel.TypeGuitar;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by thales on 06/10/17.
 */
@SuppressWarnings("javadoc")
public class StringParserTest implements ParserTest {

	private StringParser parser;

	@Before
	public final void setUp() {
		parser = new StringParser(null, null);
	}

	@Test
	public final void testString() {
		final String str = "some string";
		final BsonValue bsonValue = new BsonString(str);
		assertParserResult(parser, bsonValue, str);
	}

	@Test
	public final void testEnum() {
		final TypeGuitar type = TypeGuitar.ACOUSTIC;
		final BsonValue bsonValue = new BsonString(type.name());
		assertParserResult(parser, bsonValue, type);
	}

	@Test
	public final void testShort() {
		final short primitive = 123;
		final Short wrapper = new Short(primitive);
		final BsonInt32 bsonValue = new BsonInt32(primitive);
		assertParserResult(parser, bsonValue, primitive);
		assertParserResult(parser, bsonValue, wrapper);
	}

	@Test
	public final void testInteger() {
		final int testInt = 123;
		final Integer testInteger = new Integer(testInt);
		final BsonInt32 bsonValue = new BsonInt32(testInt);
		assertParserResult(parser, bsonValue, testInt);
		assertParserResult(parser, bsonValue, testInteger);
	}

	@Test
	public final void testLong() {
		final long testPrimitive = 123L;
		final Long testWrapper = new Long(testPrimitive);
		final BsonInt64 bsonValue = new BsonInt64(testPrimitive);
		assertParserResult(parser, bsonValue, testPrimitive);
		assertParserResult(parser, bsonValue, testWrapper);
	}

	@Test
	public final void testBigDecimal() {
		final double primDouble = Math.PI;
		final BigDecimal bigDecimal = new BigDecimal(primDouble);
		final Decimal128 decimal = new Decimal128(bigDecimal);
		final BsonDecimal128 bsonValue = new BsonDecimal128(decimal);
		assertParserResult(parser, bsonValue, decimal);
		assertParserResult(parser, bsonValue, bigDecimal);
	}

	@Test
	public final void testFloat() {
		final float primitive = 0.87f;
		final Float wrapper = new Float(primitive);
		final BsonDouble bsonValue = new BsonDouble(primitive);
		assertParserResult(parser, bsonValue, primitive);
		assertParserResult(parser, bsonValue, wrapper);
	}

	@Test
	public final void testBigDouble() {
		final double primitive = 12.12;
		final Double wrapper = new Double(primitive);
		final BsonValue bsonValue = new BsonDouble(primitive);
		assertParserResult(parser, bsonValue, primitive);
		assertParserResult(parser, bsonValue, wrapper);
	}

	@Test(expected = RuntimeException.class)
	public void fromBson_ShouldRaise_RuntimeException_IfValueIsNull() {
		parser.fromBson(null, String.class, null);
	}

	@Test
	public void isValidBson_ShouldReturn_True() {
		final boolean validBson = parser.isValidBson(new BsonString("a string"));
		assertTrue(validBson);
	}

	@Test
	public void isValidBson_ShouldReturn_False() {
		final boolean validBson = parser.isValidBson(new BsonArray());
		assertFalse(validBson);
	}

}
