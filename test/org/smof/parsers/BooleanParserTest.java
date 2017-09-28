package org.smof.parsers;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by thales minussi on 28/09/17.
 * https://github.com/tminussi
 */
public class BooleanParserTest {

    @Test
    public void serializeToBson_ShouldSerialize_SmofFieldCorrectly() {
        AbstractBsonParser abstractBsonParser = new BooleanParser(null, null);
        BsonValue bsonValue = abstractBsonParser.serializeToBson(Boolean.TRUE, null);
        assertTrue(bsonValue.isBoolean());
    }

    @Test
    public void serializeToBson_ShouldReturn_Null() {
        AbstractBsonParser abstractBsonParser = new BooleanParser(null, null);
        BsonValue bsonValue = abstractBsonParser.serializeToBson("This is not a boolean value", null);
        assertNull(bsonValue);
    }

    @Test(expected = RuntimeException.class)
    public void serializeToBson_ShouldRaise_RuntimeException_IfValueIsNull() {
        AbstractBsonParser abstractBsonParser = new BooleanParser(null, null);
        abstractBsonParser.serializeToBson(null, null);
    }

    @Test
    public void fromBson_ShouldReturn_True() {
        AbstractBsonParser abstractBsonParser = new BooleanParser(null, null);
        BsonBoolean bsonBoolean = new BsonBoolean(Boolean.TRUE);
        Boolean bool = abstractBsonParser.fromBson(bsonBoolean, Boolean.class, null);
        assertTrue(bool);
    }

    @Test(expected = RuntimeException.class)
    public void fromBson_ShouldRaise_RuntimeException_IfValueIsNull() {
        AbstractBsonParser abstractBsonParser = new BooleanParser(null, null);
        abstractBsonParser.fromBson(null, Boolean.class, null);
    }

    @Test
    public void isValidBson_ShouldReturn_True() {
        AbstractBsonParser abstractBsonParser = new BooleanParser(null, null);
        boolean validBson = abstractBsonParser.isValidBson(new BsonBoolean(Boolean.TRUE));
        assertTrue(validBson);
    }

    @Test
    public void isValidBson_ShouldReturn_False() {
        AbstractBsonParser abstractBsonParser = new BooleanParser(null, null);
        boolean validBson = abstractBsonParser.isValidBson(new BsonArray());
        assertFalse(validBson);
    }
}