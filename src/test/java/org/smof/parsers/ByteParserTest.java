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

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by thales on 01/10/17.
 */
@SuppressWarnings("javadoc")
public class ByteParserTest {

    private AbstractBsonParser parser;

    @SmofString(name = "name")
    private String name;

    @Before
    public void setup() {
        this.parser = new ByteParser(null, null);
    }
    
    private void testSupport(Object value, BsonBinary bsonValue, Class<?> type) {
		assertTrue(parser.isValidType(type));
		assertEquals(bsonValue, parser.toBson(value, null));
	}
    
    @Test
    public void primitiveByteArraySupport() {
    	final byte[] value = new byte[1024];
    	new Random().nextBytes(value);
    	final BsonBinary bsonValue = new BsonBinary(value);
    	testSupport(value, bsonValue, byte[].class);
    	assertArrayEquals(value, parser.fromBson(bsonValue, byte[].class, null));
    }
    
    @Test
    public void genericByteArraySupport() {
    	final byte[] value = new byte[1024];
    	new Random().nextBytes(value);
    	final BsonBinary bsonValue = new BsonBinary(value);
    	testSupport(ArrayUtils.toObject(value), bsonValue, Byte[].class);
    	assertArrayEquals(ArrayUtils.toObject(value), parser.fromBson(bsonValue, Byte[].class, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void serializeToBson_ShouldThrow() {
        assertNull(this.parser.serializeToBson("Not a byte array", null));
    }

    @Test
    public void serializeToBson_ShouldSerializePrimitiveByteArray_Successfully() {
        byte[] bytes = "a byte array".getBytes();
        BsonValue bsonValue = this.parser.serializeToBson(bytes, null);
        byte[] data = ((BsonBinary) bsonValue).getData();
        assertEquals(bytes, data);
    }

    @Test
    public void serializeToBson_ShouldSerializeWrapperByteArray_Successfully() {
        byte[] bytes = "a byte array".getBytes();
        Byte[] wrappedBytes = ArrayUtils.toObject(bytes);
        BsonValue bsonValue = this.parser.serializeToBson(wrappedBytes, null);
        byte[] data = ((BsonBinary) bsonValue).getData();
        assertArrayEquals(bytes, data);
    }

    @Test
    public void fromBson_ShouldParseRawValue_Correctly() {
        byte[] bytes = this.parser.fromBson(new BsonBinary("a byte array".getBytes()), byte[].class, null);
        assertArrayEquals("a byte array".getBytes(), bytes);
    }

    @Test
    public void fromBson_ShouldParseWrapperRawValue_Correctly() {
        Byte[] bytes = this.parser.fromBson(new BsonBinary("a byte array".getBytes()), Byte[].class, null);
        assertArrayEquals("a byte array".getBytes(), ArrayUtils.toPrimitive(bytes));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromBson_ShouldRaise_IllegalArgumentExceptionIfRawValueIsNull() {
        this.parser.fromBson(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromBson_ShouldRaise_IllegalArgumentExceptionIfTypeIsNull() {
        this.parser.fromBson(new BsonBinary("a byte array".getBytes()), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromBson_ShouldThrow() {
        this.parser.fromBson(new BsonBinary("a byte array".getBytes()), String.class, null);
    }

    @Test
    public void isValidBson_ShouldReturn_True() {
        this.parser = new ByteParser(null, null);
        boolean validBson = parser.isValidBson(new BsonBinary("a byte array".getBytes()));
        assertTrue(validBson);
    }

    @Test
    public void isValidBson_ShouldReturn_False() {
        boolean validBson = parser.isValidBson(new BsonArray());
        assertFalse(validBson);
    }

    @Test
    public void isValidType_ShouldReturn_False() throws NoSuchFieldException {
        this.parser = new ByteParser(null, null);
        PrimaryField fieldOpts = new PrimaryField(ByteParserTest.class.getDeclaredField("name"), SmofType.STRING) {
            @Override
            public SmofType getType() {
                return SmofType.NUMBER;
            }

        };
        boolean validType = parser.isValidType(fieldOpts);
        assertFalse(validType);
    }
}
