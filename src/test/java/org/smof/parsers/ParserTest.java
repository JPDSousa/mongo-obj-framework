package org.smof.parsers;

import static org.junit.Assert.*;

import org.bson.BsonValue;

@SuppressWarnings("javadoc")
public interface ParserTest {
	
	default void assertParserResult(AbstractBsonParser parser, BsonValue bsonValue, Object javaValue) {
		assertTrue(parser.isValidType(javaValue.getClass()));
		assertTrue(parser.isValidBson(bsonValue));
		assertEquals(bsonValue, parser.toBson(javaValue, null));
		assertEquals(javaValue, parser.fromBson(bsonValue, javaValue.getClass(), null));
	}

}
