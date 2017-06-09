package org.smof.test.field;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.Before;
import org.junit.Test;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;
import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class ParameterFieldTest {

	private static ParameterField guineaPig;
	private static PrimaryField primary;
	@SmofString(name = "paramA")
	private static String paramA;
	
	@SuppressWarnings("unused")
	private static void guineaPig(@SmofParam(name = "paramA") String paramA) {
		//dummy
	}
	
	@Before
	public final void setUp() throws NoSuchMethodException, SecurityException, NoSuchFieldException, InvalidSmofTypeException {
		final Method method = ParameterFieldTest.class.getDeclaredMethod("guineaPig", String.class);
		final Parameter param = method.getParameters()[0];
		final Field field = ParameterFieldTest.class.getDeclaredField("paramA");
		primary = new PrimaryField(field, SmofType.STRING);
		guineaPig = new ParameterField(param, param.getAnnotation(SmofParam.class));
		guineaPig.setPrimaryField(primary);
	}
	
	@Test
	public final void testGetType() {
		assertEquals(SmofType.STRING, guineaPig.getType());
		guineaPig.setPrimaryField(null);
		assertNull(guineaPig.getType());
	}

	@Test
	public final void testGetPrimaryField() {
		assertEquals(primary, guineaPig.getPrimaryField());
	}

	@Test
	public final void testSetPrimaryField() {
		guineaPig.setPrimaryField(null);
		assertNull(guineaPig.getPrimaryField());
	}

	@Test
	public final void testGetFieldClass() {
		assertEquals(String.class, guineaPig.getFieldClass());
	}

	@Test
	public final void testGetName() {
		assertEquals("paramA", guineaPig.getName());
	}

}
