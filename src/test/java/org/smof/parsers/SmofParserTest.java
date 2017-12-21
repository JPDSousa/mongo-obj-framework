package org.smof.parsers;

import static org.junit.Assert.*;

import org.bson.BsonString;
import org.bson.BsonValue;
import org.junit.Before;
import org.junit.Test;
import org.smof.element.Element;
import org.smof.field.MasterField;
import org.smof.field.SmofField;
import org.smof.parsers.metadata.SmofTypeContext;

@SuppressWarnings("javadoc")
public class SmofParserTest {
	
	private static SmofParser parser;

	@Before
	public void setUp() {
		parser = new SmofParser(null);
	}

	@Test
	public final void testGetContext() {
		assertEquals(SmofTypeContext.create(), parser.getContext());
	}

	@Test
	public final void testGetRegistry() {
		assertNotNull(parser.getRegistry());
	}

	@Test
	public final void testGetCache() {
		assertNotNull(parser.getCache());
	}

	@Test
	public final void testToBsonObjectClassOfT() {
		// TODO add more tests
		final BsonValue expected = new BsonString("somethingOutOfNothing");
		assertEquals(expected, parser.toBson(expected, Object.class));
		assertEquals(expected, parser.toBson(expected, (Class<?>) null));
	}

	@Test
	public final void testToBsonObjectSmofField() {
		// TODO add more tests
		final BsonValue expected = new BsonString("somethingOutOfNothing");
		assertEquals(expected, parser.toBson(expected, new MasterField(Element.class)));
		assertEquals(expected, parser.toBson(expected, (SmofField) null));
	}

}
