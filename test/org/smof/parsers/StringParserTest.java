package org.smof.parsers;

import org.bson.*;
import org.junit.Test;
import org.smof.dataModel.TypeGuitar;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by thales on 06/10/17.
 */
public class StringParserTest {

    @Test
    public void serializeToBson_ShouldSerialize_ObjectStringValueCorrectly() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonValue bsonValue = abstractBsonParser.serializeToBson("some string", null);
        assertEquals("BsonString{value='some string'}", bsonValue.toString());
    }

    @Test
    public void serializeToBson_ShouldSerialize_ObjectEnumValueCorrectly() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonValue bsonValue = abstractBsonParser.serializeToBson(TypeGuitar.ACOUSTIC, null);
        assertEquals("BsonString{value='ACOUSTIC'}", bsonValue.toString());
    }

    @Test
    public void serializeToBson_ShouldSerialize_ObjectIntegerValueCorrectly() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonValue bsonValue = abstractBsonParser.serializeToBson(1, null);
        assertEquals("BsonString{value='1'}", bsonValue.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void serializeToBson_ShouldSerialize_ToNullAnd_RaiseException() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        abstractBsonParser.serializeToBson(BigDecimal.ONE, null);
    }

    @Test
    public void fromBson_ShouldReturn_StringRawValue() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonString bsonString = new BsonString("bsonString");
        String string = abstractBsonParser.fromBson(bsonString, String.class, null);
        assertEquals("bsonString", string);
    }

    @Test
    public void fromBson_ShouldReturn_IntegerRawValue() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonString bsonString = new BsonString("1");
        Integer integer = abstractBsonParser.fromBson(bsonString, Integer.class, null);
        assertEquals(1, integer.intValue());
    }

    @Test(expected = RuntimeException.class)
    public void fromBson_ShouldRaise_RuntimeException_IfValueIsNull() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        abstractBsonParser.fromBson(null, Boolean.class, null);
    }

    @Test
    public void isValidBson_ShouldReturn_True() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        boolean validBson = abstractBsonParser.isValidBson(new BsonString("a string"));
        assertTrue(validBson);
    }

    @Test
    public void isValidBson_ShouldReturn_False() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        boolean validBson = abstractBsonParser.isValidBson(new BsonArray());
        assertFalse(validBson);
    }

}