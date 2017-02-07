package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.element.AbstractElement;
import org.smof.element.ElementTypeFactory;
import org.smof.element.field.SmofDate;
import org.smof.element.field.SmofInnerObject;
import org.smof.element.field.SmofNumber;
import org.smof.element.field.SmofObjectId;
import org.smof.element.field.SmofString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("javadoc")
public class ElementTypeFactoryTests {
	
	private static Gson gson;

	@BeforeClass
	public static void setUpBeforeClass() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapterFactory(ElementTypeFactory.getDefault());
		
		gson = builder.create();
	}

	@Test
	public void test() {
		final String testStr = "test";
		final int testInt = 31;
		final ObjectId objId = new ObjectId();
		final Instant date = Instant.now();
		final ElementTestA el1 = new ElementTestA(testStr, testInt, objId, date);
		final ElementTest guineaPig = new ElementTest(testStr, testInt, objId, date, el1);
		
		System.out.println(gson.toJson(guineaPig));
	}
	
	
	private class ElementTest extends AbstractElement {
		
		@SmofString(name = "str1", required = true)
		private final String str1;
		
		@SmofNumber(name = "int1")
		private final int int1;
		
		@SmofObjectId(name = "objId", ref = "coll1")
		private final ObjectId objId;
		
		@SmofDate(name = "date")
		private final Instant date;
		
		@SmofInnerObject(name = "el1", required = true)
		private final ElementTestA el1;
		
		private ElementTest(String str1, int int1, ObjectId objId, Instant date, ElementTestA el1) {
			this.str1 = str1;
			this.int1 = int1;
			this.objId = objId;
			this.date = date;
			this.el1 = el1;
		}
		
	}
	
	private class ElementTestA {
		
		@SmofString(name = "str1", required = true)
		private final String str1;
		
		@SmofNumber(name = "int1")
		private final int int1;
		
		@SmofObjectId(name = "objId", ref = "coll1")
		private final ObjectId objId;
		
		@SmofDate(name = "date")
		private final Instant date;
		
		private ElementTestA(String str1, int int1, ObjectId objId, Instant date) {
			this.str1 = str1;
			this.int1 = int1;
			this.objId = objId;
			this.date = date;
		}
		
	}

}
