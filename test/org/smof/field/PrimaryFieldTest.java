package org.smof.field;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import javax.lang.model.element.Element;

import org.junit.Before;
import org.junit.Test;
import org.smof.annnotations.SmofBoolean;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofString;
import org.smof.field.MasterField;
import org.smof.field.PrimaryField;
import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class PrimaryFieldTest {

	private static final SmofType TYPE = SmofType.STRING;
	private static final String NAME = "test";
	
	private static PrimaryField guineaPig;
	
	@SmofNumber(name = NAME)
	@SmofString(name = NAME)
	private static String fieldTest1;
	private static Field fieldTestRef1;
	
	@SmofString(name = "test2")
	@SmofBoolean(name = "test2")
	private static Boolean fieldTest2;
	
	@Before
	public void setUp() throws NoSuchFieldException, SecurityException {
		fieldTestRef1 = PrimaryFieldTest.class.getDeclaredField("fieldTest1");
		guineaPig = new PrimaryField(fieldTestRef1, TYPE);
	}

	@Test
	public final void testHashCode() {
		assertEquals(guineaPig.hashCode(), new PrimaryField(fieldTestRef1, SmofType.NUMBER).hashCode());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void testPrimaryFieldNullField() {
		final PrimaryField field = new PrimaryField(null, null);
		field.hashCode();
	}

	@Test
	public final void testGetType() {
		assertEquals(TYPE, guineaPig.getType());
	}

	@Test
	public final void testGetName() {
		assertEquals(NAME, guineaPig.getName());
	}

	@Test
	public final void testIsRequired() {
		assertFalse(guineaPig.isRequired());
	}

	@Test
	public final void testGetSmofAnnotationAs() {
		assertEquals(SmofString.class, guineaPig.getSmofAnnotationAs(SmofString.class).annotationType());
	}

	@Test
	public final void testGetRawField() {
		assertEquals(fieldTestRef1, guineaPig.getRawField());
	}

	@Test
	public final void testCompareTo() throws NoSuchFieldException, SecurityException {
		final Field testRef = PrimaryFieldTest.class.getDeclaredField("fieldTest2");
		assertTrue(guineaPig.compareTo(new PrimaryField(testRef, SmofType.BOOLEAN)) != 0);
		assertTrue(guineaPig.compareTo(new PrimaryField(testRef, SmofType.STRING)) != 0);
	}

	@Test
	public final void testIsExternal() {
		assertFalse(guineaPig.isExternal());
	}

	@Test
	public final void testGetFieldClass() {
		assertEquals(fieldTestRef1.getType(), guineaPig.getFieldClass());
	}

	@Test
	public final void testIsBuilder() {
		assertFalse(guineaPig.isBuilder());
	}

	@Test
	public final void testSetBuilder() {
		guineaPig.setBuilder(true);
		assertTrue(guineaPig.isBuilder());
	}

	@Test
	public final void testEqualsObject() {
		assertEquals(guineaPig, new PrimaryField(fieldTestRef1, SmofType.NUMBER));
		assertEquals(guineaPig, guineaPig);
		assertNotEquals(guineaPig, null);
		assertNotEquals(guineaPig, new MasterField(Element.class));
	}

}
