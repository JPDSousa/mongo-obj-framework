package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;

import org.smof.element.AbstractElement;
import org.smof.element.SmofAdapterPool;
import org.smof.element.SmofAdapter;
import org.smof.element.field.SmofArray;
import org.smof.element.field.SmofBuilder;
import org.smof.element.field.SmofDate;
import org.smof.element.field.SmofObject;
import org.smof.element.field.SmofNumber;
import org.smof.element.field.SmofObjectId;
import org.smof.element.field.SmofParam;
import org.smof.element.field.SmofString;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.NoSuchAdapterException;
import org.smof.exception.SmofException;
import org.smof.element.field.SmofField.FieldType;

@SuppressWarnings("javadoc")
public class ElementTypeFactoryTests {
	
	private static SmofAdapterPool adapters;
	
	@BeforeClass
	public static void setUpBeforeClass() throws InvalidSmofTypeException {
		adapters = new SmofAdapterPool();
		
		adapters.put(ElStrTest.class);
		adapters.put(ElObjIdTest.class);
		adapters.put(ElNumTest.class);
		adapters.put(ElDateTest.class);
//		adapters.put(ElObjTest.class);
//		adapters.put(ElObjTest.ElObjTestB.class);
//		adapters.put(ElObjTest.ElObjTestA.class);
//		adapters.put(ElArrTest.class);		
	}

	@Test
	public void testString() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElStrTest> parser = adapters.get(ElStrTest.class);
		final ElStrTest test = new ElStrTest("test", ElStrTest.EnumTest.VALB);
		test.str2 = "askjdahsj";
		final BsonDocument doc = parser.write(test);
		assertEquals(test, parser.read(doc));
	}
	
	@Test
	public void testObjectId() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElObjIdTest> parser = adapters.get(ElObjIdTest.class);
		final ElObjIdTest test = new ElObjIdTest(new ObjectId());
		final BsonDocument doc = parser.write(test);
		assertEquals(test, parser.read(doc));
	}
	
	@Test
	public void testNumber() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElNumTest> parser = adapters.get(ElNumTest.class);
		final ElNumTest test = new ElNumTest(31, new Long(31), new Short((short) 200));
		final BsonDocument doc = parser.write(test);
		assertEquals(test, parser.read(doc));
	}
	
	@Test
	public void testDate() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElDateTest> parser = adapters.get(ElDateTest.class);
		final ElDateTest test = new ElDateTest(Instant.now(), LocalDate.now(), LocalDateTime.now());
		final BsonDocument doc = parser.write(test);
		assertEquals(test, parser.read(doc));
	}
	
	@Test
	public void testObject() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElObjTest> parser = adapters.get(ElObjTest.class); 
		System.out.println(parser.write(new ElObjTest()).toJson());
	}
	
	@Test
	public void testArray() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElArrTest> parser = adapters.get(ElArrTest.class); 
		System.out.println(parser.write(new ElArrTest()).toJson());
	}
	
	private static class ElStrTest extends AbstractElement {
		
		@SmofString(name = "str1")
		private final String str1;
		
		@SmofString(name = "en1")
		private final EnumTest en1;
		
		@SmofString(name = "str2")
		private String str2;
		
		@SmofBuilder
		private ElStrTest(@SmofParam(name = "str1") String str1, @SmofParam(name ="en1") EnumTest en1) {
			this.str1 = str1;
			this.en1 = en1;
		}
		
		private enum EnumTest {
			VALA,
			VALB,
			VALC;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ElStrTest other = (ElStrTest) obj;
			if (en1 != other.en1) {
				return false;
			}
			if (str1 == null) {
				if (other.str1 != null) {
					return false;
				}
			} else if (!str1.equals(other.str1)) {
				return false;
			}
			if (str2 == null) {
				if (other.str2 != null) {
					return false;
				}
			} else if (!str2.equals(other.str2)) {
				return false;
			}
			return true;
		}
	}
	
	private static class ElObjIdTest extends AbstractElement{

		@SmofObjectId(name = "objId", ref = "coll1")
		private final ObjectId objId;
		
		@SmofBuilder
		public ElObjIdTest(@SmofParam(name="objId") ObjectId id) {
			this.objId = id;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ElObjIdTest other = (ElObjIdTest) obj;
			if (objId == null) {
				if (other.objId != null) {
					return false;
				}
			} else if (!objId.equals(other.objId)) {
				return false;
			}
			return true;
		}
	}
	
	private static class ElNumTest extends AbstractElement{
		
		@SmofNumber(name = "int")
		private final int int1;
		
		@SmofNumber(name = "long")
		private final long long1;
		
		@SmofNumber(name = "short")
		private final short short1;

		@SmofBuilder
		private ElNumTest(@SmofParam(name = "int") Integer int1, 
				@SmofParam(name = "long") Long long1, 
				@SmofParam(name = "short") Short short1) {
			this.int1 = int1;
			this.long1 = long1;
			this.short1 = short1;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ElNumTest other = (ElNumTest) obj;
			if (int1 != other.int1) {
				return false;
			}
			if (long1 != other.long1) {
				return false;
			}
			if (short1 != other.short1) {
				return false;
			}
			return true;
		}
	}
	
	private static class ElDateTest extends AbstractElement {
		
		@SmofDate(name="date")
		private final Instant date;
		
		@SmofDate(name="localdate")
		private final LocalDate localdate;
		
		@SmofDate(name="localdateTime")
		private final LocalDateTime localdateTime;

		@SmofBuilder
		private ElDateTest(@SmofParam(name = "date") Instant date, 
				@SmofParam(name = "localdate") LocalDate localdate, 
				@SmofParam(name = "localdatetime") LocalDateTime localdateTime) {
			super();
			this.date = date;
			this.localdate = localdate;
			this.localdateTime = localdateTime;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ElDateTest other = (ElDateTest) obj;
			if (date == null) {
				if (other.date != null) {
					return false;
				}
			} else if (!date.equals(other.date)) {
				return false;
			}
			if (localdate == null) {
				if (other.localdate != null) {
					return false;
				}
			} else if (!localdate.equals(other.localdate)) {
				return false;
			}
			if (localdateTime == null) {
				if (other.localdateTime != null) {
					return false;
				}
			} else if (!localdateTime.equals(other.localdateTime)) {
				return false;
			}
			return true;
		}
		
		
	}
	
	private static class ElObjTest extends AbstractElement {
		
		@SmofObject(name = "el1")
		private final ElObjTestA el1;
		
		@SmofObject(name = "el2")
		private final ElObjTestB el2;
		
		public ElObjTest() {
			el1 = new ElObjTestA();
			el2 = new ElObjTestB();
		}

		private static class ElObjTestA {
			
			@SmofNumber(name = "int1")
			private final int int1;
			
			@SmofString(name = "str1")
			private final String str1;
			
			@SmofObject(name = "el1")
			private final ElObjTestB elA;
			
			public ElObjTestA() {
				int1 = 20;
				str1 = "gauss";
				elA = new ElObjTestB();
			}
		}
		
		private static class ElObjTestB extends AbstractElement {

			@SmofNumber(name = "int1")
			private final int int1;
			
			@SmofString(name = "str1")
			private final String str1;
			
			public ElObjTestB() {
				int1 = 20;
				str1 = "gauss";
			}
		}
	}
	
	private static class ElArrTest extends AbstractElement {
		
		@SmofArray(name = "arr1", type = FieldType.NUMBER)
		private final int[] arr1;
		
		@SmofArray(name = "arr2", type = FieldType.DATE)
		private final LocalDate[] arr2;
		
		public ElArrTest() {
			this.arr1 = new int[500];
			Arrays.fill(arr1, 30);
			this.arr2 = new LocalDate[]{LocalDate.now(), LocalDate.now()};
		}
	}

}
