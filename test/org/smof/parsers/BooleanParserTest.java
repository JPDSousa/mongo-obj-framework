package org.smof.parsers;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by thales minussi on 28/09/17.
 * https://github.com/tminussi
 */
@SuppressWarnings("javadoc")
public class BooleanParserTest {

	private static final BsonValue BSON_TRUE = BsonBoolean.TRUE;
	private static final BsonValue BSON_FALSE = BsonBoolean.FALSE;
	private static AbstractBsonParser parser;

	@Before
	public void setUp() {
		parser = new BooleanParser(null, null);
	}
	
	private void testSupport(BsonValue bsonTrue, BsonValue bsonFalse, 
			Object valueTrue, Object valueFalse, Class<?> type) {
		// valid type
		assertTrue(parser.isValidType(type));
		// valid bson
		assertTrue(parser.isValidBson(bsonTrue));
		assertTrue(parser.isValidBson(bsonFalse));
		// valid serialization
		assertEquals(bsonTrue, parser.toBson(valueTrue, null));
		assertEquals(bsonFalse, parser.toBson(valueFalse, null));
		// valid deserialization
		assertEquals(valueTrue, parser.fromBson(bsonTrue, type, null));
		assertEquals(valueFalse, parser.fromBson(bsonFalse, type, null));
	}

	@Test
	public void booleanSupport() {
		testSupport(BSON_TRUE, BSON_FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.class);
		testSupport(BSON_TRUE, BSON_FALSE, true, false, boolean.class);
	}
	
	@Test
	public void stringSupport() {
		final String valueTrue = Boolean.TRUE.toString();
		final String valueFalse = Boolean.FALSE.toString();
		final BsonValue bsonTrue = new BsonString(valueTrue);
		final BsonValue bsonFalse = new BsonString(valueFalse);
		testSupport(bsonTrue, bsonFalse, valueTrue, valueFalse, String.class);
	}
	
	@Test
	public void integerSupport() {
		final int valueTrue = 1;
		final int valueFalse = 0;
		final BsonValue bsonTrue = new BsonInt32(valueTrue);
		final BsonValue bsonFalse = new BsonInt32(valueFalse);
		testSupport(bsonTrue, bsonFalse, valueTrue, valueFalse, Integer.class);
		testSupport(bsonTrue, bsonFalse, valueTrue, valueFalse, int.class);
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
		BsonValue bsonValue = parser.serializeToBson("This is not a boolean value", null);
		assertNull(bsonValue);
	}

	@Test(expected = RuntimeException.class)
	public void serializeToBson_ShouldRaise_RuntimeException_IfValueIsNull() {
		parser.serializeToBson(null, null);
	}

	@Test
	public void isValidBson_ShouldReturn_True() {
		boolean validBson = parser.isValidBson(new BsonBoolean(Boolean.TRUE));
		assertTrue(validBson);
	}

	@Test
	public void isValidBson_ShouldReturn_False() {
		boolean validBson = parser.isValidBson(new BsonArray());
		assertFalse(validBson);
	}
}