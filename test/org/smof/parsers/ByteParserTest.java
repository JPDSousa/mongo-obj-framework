package org.smof.parsers;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonValue;
import org.junit.Before;
import org.junit.Test;
import org.smof.annnotations.SmofString;
import org.smof.field.MasterField;
import org.smof.field.PrimaryField;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by thales on 01/10/17.
 */
@SuppressWarnings("javadoc")
public class ByteParserTest {

    private AbstractBsonParser abstractBsonParser;

    @SmofString(name = "name")
    private String name;

    @Before
    public void setup() {
        this.abstractBsonParser = new ByteParser(null, null);
    }

    @Test
    public void serializeToBson_ShouldReturn_Null() {
        assertNull(this.abstractBsonParser.serializeToBson("Not a byte array", null));
    }

    @Test
    public void serializeToBson_ShouldSerializePrimitiveByteArray_Successfully() {
        byte[] bytes = "a byte array".getBytes();
        BsonValue bsonValue = this.abstractBsonParser.serializeToBson(bytes, null);
        byte[] data = ((BsonBinary) bsonValue).getData();
        assertEquals(bytes, data);
    }

    @Test
    public void serializeToBson_ShouldSerializeWrapperByteArray_Successfully() {
        byte[] bytes = "a byte array".getBytes();
        Byte[] wrappedBytes = ArrayUtils.toObject(bytes);
        BsonValue bsonValue = this.abstractBsonParser.serializeToBson(wrappedBytes, null);
        byte[] data = ((BsonBinary) bsonValue).getData();
        assertArrayEquals(bytes, data);
    }

    @Test
    public void serializeToBson_ShouldSerializeByteCollection_Successfully() {
        Collection<Byte> bytes = new ArrayList<>();
        bytes.add(ArrayUtils.toObject("a byte array".getBytes())[0]);
        BsonValue bsonValue = this.abstractBsonParser.serializeToBson(bytes, null);
        byte[] data = ((BsonBinary) bsonValue).getData();
        assertArrayEquals("a".getBytes(), data);
    }

    @Test
    public void fromBson_ShouldParseRawValue_Correctly() {
        byte[] bytes = this.abstractBsonParser.fromBson(new BsonBinary("a byte array".getBytes()), byte[].class, null);
        assertArrayEquals("a byte array".getBytes(), bytes);
    }

    @Test
    public void fromBson_ShouldParseWrapperRawValue_Correctly() {
        Byte[] bytes = this.abstractBsonParser.fromBson(new BsonBinary("a byte array".getBytes()), Byte[].class, null);
        assertArrayEquals("a byte array".getBytes(), ArrayUtils.toPrimitive(bytes));
    }

    @Test
    public void fromBson_ShouldParseRawValueCollection_Correctly() {
        Collection<byte[]> bytes = new ArrayList<>();
        bytes.add("a byte array".getBytes());
        this.abstractBsonParser.fromBson(new BsonBinary("a byte array".getBytes()), bytes.getClass(), null);
        assertArrayEquals("a byte array".getBytes(), bytes.stream().findAny().get());
    }

    @Test(expected = RuntimeException.class)
    public void fromBson_ShouldRaise_RuntimeExceptionIfRawValueIsNull() {
        this.abstractBsonParser.fromBson(null, null, null);
    }

    @Test(expected = RuntimeException.class)
    public void fromBson_ShouldRaise_RuntimeExceptionIfTypeIsNull() {
        this.abstractBsonParser.fromBson(new BsonBinary("a byte array".getBytes()), null, null);
    }

    @Test
    public void fromBson_ShouldReturn_Null() {
        this.abstractBsonParser.fromBson(new BsonBinary("a byte array".getBytes()), String.class, null);
    }

    @Test
    public void isValidBson_ShouldReturn_True() {
        this.abstractBsonParser = new ByteParser(null, null);
        boolean validBson = abstractBsonParser.isValidBson(new BsonBinary("a byte array".getBytes()));
        assertTrue(validBson);
    }

    @Test
    public void isValidBson_ShouldReturn_False() {
        boolean validBson = abstractBsonParser.isValidBson(new BsonArray());
        assertFalse(validBson);
    }

    @Test
    public void isValidType_ShouldReturn_True() {
        this.abstractBsonParser = new ByteParser(null, null);
        boolean validType = abstractBsonParser.isValidType(new MasterField(String.class));
        assertTrue(validType);
    }

    @Test
    public void isValidType_ShouldReturn_False() throws NoSuchFieldException {
        this.abstractBsonParser = new ByteParser(null, null);
        PrimaryField fieldOpts = new PrimaryField(ByteParserTest.class.getDeclaredField("name"), SmofType.STRING) {
            @Override
            public SmofType getType() {
                return SmofType.NUMBER;
            }

        };
        boolean validType = abstractBsonParser.isValidType(fieldOpts);
        assertFalse(validType);
    }
}