package org.smof.parsers;

import org.bson.*;
import org.bson.types.Decimal128;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class NumberParserTest {

	private NumberParser parser;

	@Before
	public void setUp() {
		parser = new NumberParser(null, null);
	}

	@Test
	public void testInteger() {
		Number testNumber = 123;
		BsonValue bsonValue = parser.serializeToBson(testNumber, null);
		Assert.assertEquals(new BsonInt32(123), bsonValue);
	}

	@Test
	public void testLong() {
		Number testNumber = 123L;
		BsonValue bsonValue = parser.serializeToBson(testNumber, null);
		Assert.assertEquals(new BsonInt64(123), bsonValue);
	}

	@Test
	public void testBigDecimal() {
		Number testNumber = new BigDecimal("3.141592654");
		BsonValue bsonValue = parser.serializeToBson(testNumber, null);
		Assert.assertEquals(new BsonDecimal128(Decimal128.parse("3.141592654")), bsonValue);
	}

  @Test
  public void testBigDouble() {
    Number testNumber = 12.12;
    BsonValue bsonValue = parser.serializeToBson(testNumber, null);
    System.out.println(bsonValue.getClass().getCanonicalName());
    Assert.assertEquals(new BsonDouble(12.12), bsonValue);
  }

}
