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

import org.apache.commons.lang3.ArrayUtils;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonValue;
import org.junit.Before;
import org.junit.Test;
import org.smof.annnotations.SmofString;
import org.smof.field.PrimaryField;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class ByteParserTest implements ParserTest {

	private ByteParser parser;

	@SmofString(name = "name")
	private String name;

	@Before
	public void setup() {
		parser = new ByteParser(null, null);
	}
	
	@Test
	public final void testByteArray() {
		final Random random = new Random();
		final byte[] primitive = new byte[1024];
		random.nextBytes(primitive);
		final Byte[] wrapper = ArrayUtils.toObject(primitive);
		final Collection<Byte> collection = Arrays.asList(wrapper);
		final BsonValue bsonValue = new BsonBinary(primitive);
		assertParserResult(parser, bsonValue, primitive);
		assertParserResult(parser, bsonValue, wrapper);
		assertParserResult(parser, bsonValue, collection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void serializeToBson_ShouldThrow() {
		assertNull(parser.toBson("Not a byte array", null));
	}

	@Test
	public void serializeToBson_ShouldSerializePrimitiveByteArray_Successfully() {
		final byte[] bytes = "a byte array".getBytes();
		final BsonValue bsonValue = parser.toBson(bytes, null);
		final byte[] data = ((BsonBinary) bsonValue).getData();
		assertEquals(bytes, data);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromBson_ShouldRaise_IllegalArgumentExceptionIfRawValueIsNull() {
		parser.fromBson(null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromBson_ShouldRaise_IllegalArgumentExceptionIfTypeIsNull() {
		parser.fromBson(new BsonBinary("a byte array".getBytes()), null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromBson_ShouldThrow() {
		parser.fromBson(new BsonBinary("a byte array".getBytes()), String.class, null);
	}

	@Test
	public void isValidBson_ShouldReturn_True() {
		final boolean validBson = parser.isValidBson(new BsonBinary("a byte array".getBytes()));
		assertTrue(validBson);
	}

	@Test
	public void isValidBson_ShouldReturn_False() {
		final boolean validBson = parser.isValidBson(new BsonArray());
		assertFalse(validBson);
	}

	@Test
	public void isValidType_ShouldReturn_False() throws NoSuchFieldException {
		PrimaryField fieldOpts = new PrimaryField(ByteParserTest.class.getDeclaredField("name"), SmofType.STRING) {
			@Override
			public SmofType getType() {
				return SmofType.NUMBER;
			}

		};
		final boolean validType = parser.isValidType(fieldOpts);
		assertFalse(validType);
	}
}
