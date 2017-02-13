package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.element.AbstractElement;
import org.smof.element.Element;
import org.smof.element.ElementFactory;
import org.smof.element.ElementFactoryPool;
import org.smof.element.ElementFieldMap;
import org.smof.element.ElementTypeFactory;
import org.smof.element.field.SmofArray;
import org.smof.element.field.SmofDate;
import org.smof.element.field.SmofField;
import org.smof.element.field.SmofObject;
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
		final ElementFactoryPool factories;
		factories = new ElementFactoryPool();
		factories.put(ElStrTest.class, new ElStrTest());
		factories.put(ElObjIdTest.class, new ElObjIdTest());
		factories.put(ElNumTest.class, new ElNumTest());
		factories.put(ElDateTest.class, new ElDateTest());
		factories.put(ElObjTest.class, new ElObjTest());
		factories.put(ElObjTest.ElObjTestB.class, new ElObjTest.ElObjTestB());
		factories.put(ElArrTest.class, new ElArrTest());
		
		ElementTypeFactory.init(factories);
		builder.registerTypeAdapterFactory(ElementTypeFactory.getDefault());
		
		gson = builder.create();
	}

	@Test
	public void test() {
		final List<Object> pigs = new ArrayList<>();
		pigs.add(new ElStrTest());
		pigs.add(new ElNumTest());
		pigs.add(new ElDateTest());
		pigs.add(new ElObjTest());
		pigs.add(new ElArrTest());
		pigs.add(new ElObjIdTest());
		
		pigs.forEach(p -> System.out.println(gson.toJson(p)));
	}
	
	private static class ElStrTest extends AbstractElement implements ElementFactory<ElStrTest> {
		
		@SmofString(name = "str1")
		private final String str1;
		
		@SmofString(name = "en1")
		private final EnumTest en1;
		
		@SmofString(name = "col1")
		private final Collection<String> col1;
		
		@SmofString(name = "int1")
		private final int int1;
		
		private ElStrTest() {
			str1 = "test";
			en1 = EnumTest.VALB;
			col1 = Arrays.asList("as", "sd");
			int1 = 31;
		}
		
		private enum EnumTest {
			VALA,
			VALB,
			VALC;
		}

		@Override
		public ElStrTest createElement(ElementFieldMap map) {
			return new ElStrTest();
		}
	}
	
	private static class ElObjIdTest extends AbstractElement implements ElementFactory<ElObjIdTest>{

		@SmofObjectId(name = "objId", ref = "coll1")
		private final ObjectId objId;
		
		public ElObjIdTest() {
			this.objId = new ObjectId();
		}

		@Override
		public ElObjIdTest createElement(ElementFieldMap map) {
			return new ElObjIdTest();
		}
	}
	
	private static class ElNumTest extends AbstractElement implements ElementFactory<ElNumTest> {
		
		@SmofNumber(name = "int")
		private final int int1;
		
		@SmofNumber(name = "long")
		private final long long1;
		
		@SmofNumber(name = "short")
		private final short short1;
		
		public ElNumTest() {
			int1 = 31;
			long1 = 31;
			short1 = 31;
		}

		@Override
		public ElNumTest createElement(ElementFieldMap map) {
			return new ElNumTest();
		}
	}
	
	private static class ElDateTest extends AbstractElement implements ElementFactory<ElDateTest> {
		
		@SmofDate(name="date")
		private final Instant date;
		
		@SmofDate(name="localdate")
		private final LocalDate localdate;
		
		@SmofDate(name="localdateTime")
		private final LocalDateTime localdateTime;
		
		public ElDateTest() {
			date = Instant.now();
			localdate = LocalDate.now();
			localdateTime = LocalDateTime.now();
		}

		@Override
		public ElDateTest createElement(ElementFieldMap map) {
			return new ElDateTest();
		}
	}
	
	private static class ElObjTest extends AbstractElement implements ElementFactory<ElObjTest> {
		
		@SmofObject(name = "el1")
		private final ElObjTestA el1;
		
		@SmofObject(name = "el2")
		private final ElObjTestB el2;
		
		public ElObjTest() {
			el1 = new ElObjTestA();
			el2 = new ElObjTestB();
		}
		
		@Override
		public ElObjTest createElement(ElementFieldMap map) {
			return new ElObjTest();
		}

		private static class ElObjTestA {
			
			@SmofNumber(name = "int1")
			private final int int1;
			
			@SmofString(name = "str1")
			private final String str1;
			
			public ElObjTestA() {
				int1 = 20;
				str1 = "gauss";
			}
		}
		
		private static class ElObjTestB extends AbstractElement implements ElementFactory<ElObjTestB>{

			@SmofNumber(name = "int1")
			private final int int1;
			
			@SmofString(name = "str1")
			private final String str1;
			
			public ElObjTestB() {
				int1 = 20;
				str1 = "gauss";
			}

			@Override
			public ElObjTestB createElement(ElementFieldMap map) {
				return new ElObjTestB();
			}
		}
	}
	
	private static class ElArrTest extends AbstractElement implements ElementFactory<ElArrTest>{
		
		@SmofArray(name = "arr1", type = SmofField.NUMBER)
		private final int[] arr1;
		
		@SmofArray(name = "arr2", type = SmofField.DATE)
		private final LocalDate[] arr2;
		
		public ElArrTest() {
			this.arr1 = new int[]{2, 3, -1};
			this.arr2 = new LocalDate[]{LocalDate.now(), LocalDate.now()};
		}

		@Override
		public ElArrTest createElement(ElementFieldMap map) {
			return new ElArrTest();
		}
	}

}
