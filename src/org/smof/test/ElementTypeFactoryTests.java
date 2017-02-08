package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;

import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.element.AbstractElement;
import org.smof.element.ElementTypeFactory;
import org.smof.element.field.SmofArray;
import org.smof.element.field.SmofDate;
import org.smof.element.field.SmofField;
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
		final ElementTest guineaPig = new ElementTest();
		
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
		
		@SmofArray(name = "arr1", type = SmofField.NUMBER)
		private final int[] arr1;
		
		@SmofArray(name = "arr2", type = SmofField.DATE)
		private final LocalDate[] arr2;
		
		private ElementTest() {
			this.str1 = "test";
			this.int1 = 31;
			this.objId = new ObjectId();
			this.date = Instant.now();
			this.el1 = new ElementTestA(str1, int1, objId, date);
			this.arr1 = new int[]{2, 3, -1};
			this.arr2 = new LocalDate[]{LocalDate.now(), LocalDate.now()};
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
