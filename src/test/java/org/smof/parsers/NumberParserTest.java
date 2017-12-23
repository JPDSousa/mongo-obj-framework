package org.smof.parsers;

import org.bson.*;
import org.bson.types.Decimal128;
import org.junit.Before;
import org.junit.Test;
import org.smof.bson.codecs.date.JavaDurationCodec;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Random;

@SuppressWarnings("javadoc")
public class NumberParserTest implements ParserTest {

	private NumberParser parser;

	@Before
	public final void setUp() {
		parser = new NumberParser(null, null);
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
	
	@Test
	public final void testDuration() {
		final Random random = new Random();
		final Duration duration = Duration.ofNanos(random.nextLong());
		final BsonValue bsonValue = new BsonDocument()
				.append(JavaDurationCodec.SECONDS, new BsonInt64(duration.getSeconds()))
				.append(JavaDurationCodec.NANOS, new BsonInt32(duration.getNano()));
		assertParserResult(parser, bsonValue, duration);
	}

}
