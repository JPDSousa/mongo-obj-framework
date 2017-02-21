package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofDate;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofObjectId;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;
import org.smof.element.AbstractElement;
import org.smof.exception.SmofException;
import org.smof.parsers.SmofParser;
import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class ElementTypeFactoryTests {
	
	private static SmofParser parser;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		parser = new SmofParser();
		
		parser.registerType(ElStrTest.class);
		parser.registerType(ElObjIdTest.class);
		parser.registerType(ElNumTest.class);
		parser.registerType(ElDateTest.class);
		parser.registerType(ElObjTest.class);
		parser.registerType(ElObjTest.ElObjTestB.class);
		parser.registerType(ElObjTest.ElObjTestA.class);
		parser.registerType(ElArrTest.class);		
	}

	@Test
	public void testString() throws SmofException {
		final ElStrTest test = new ElStrTest("test", ElStrTest.EnumTest.VALB);
		test.str2 = "askjdahsj";
		final BsonDocument doc = parser.toBson(test);
		assertEquals(test, parser.fromBson(doc, ElStrTest.class));
	}
	
	@Test
	public void testObjectId() throws SmofException {
		final ElObjIdTest test = new ElObjIdTest(new ObjectId());
		final BsonDocument doc = parser.toBson(test);
		assertEquals(test, parser.fromBson(doc, ElObjIdTest.class));
	}
	
	@Test
	public void testNumber() throws SmofException {
		final ElNumTest test = new ElNumTest(31, new Long(31), new Short((short) 200));
		final BsonDocument doc = parser.toBson(test);
		assertEquals(test, parser.fromBson(doc, ElNumTest.class));
	}
	
	@Test
	public void testDate() throws SmofException {
		final ElDateTest test = new ElDateTest(Instant.now(), LocalDate.now(), LocalDateTime.now());
		final BsonDocument doc = parser.toBson(test);
		assertEquals(test, parser.fromBson(doc, ElDateTest.class));
	}
	
	@Test
	public void testObject() throws SmofException {
		final ElObjTest.ElObjTestA a = new ElObjTest.ElObjTestA(30, "gauss");
		final ElObjTest test = new ElObjTest(a);
		test.map1 = new LinkedHashMap<>();
		for(int i=0;i<20;i++) {
			test.map1.put(i+"", Instant.now());
		}
		
		final BsonDocument doc = parser.toBson(test);
		assertEquals(test, parser.fromBson(doc, ElObjTest.class));
	}
	
	@Test
	public void testArray() throws SmofException {
		final ElArrTest test = new ElArrTest(Arrays.asList(500), Arrays.asList(LocalDate.now(), LocalDate.now()));
		final BsonDocument doc = parser.toBson(test);
		assertEquals(test, parser.fromBson(doc, ElArrTest.class));
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
		
		@SmofObject(name = "map1", mapValueType = SmofType.DATETIME)
		private Map<String, Instant> map1;
		
		@SmofBuilder
		public ElObjTest(@SmofParam(name="el1") ElObjTestA el1) {
			this.el1 = el1;
		}

		private static class ElObjTestA {
			
			@SmofNumber(name = "int1")
			private final int int1;
			
			@SmofString(name = "str1")
			private final String str1;
			
			@SmofBuilder
			public ElObjTestA(@SmofParam(name="int1")Integer int1, @SmofParam(name="str1")String str1) {
				this.int1 = int1;
				this.str1 = str1;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + int1;
				result = prime * result + ((str1 == null) ? 0 : str1.hashCode());
				return result;
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
				ElObjTestA other = (ElObjTestA) obj;
				if (int1 != other.int1) {
					return false;
				}
				if (str1 == null) {
					if (other.str1 != null) {
						return false;
					}
				} else if (!str1.equals(other.str1)) {
					return false;
				}
				return true;
			}
		}
		
		private static class ElObjTestB extends AbstractElement {

			@SmofNumber(name = "int1")
			private final int int1;
			
			@SmofString(name = "str1")
			private final String str1;
			
			@SmofBuilder
			public ElObjTestB() {
				int1 = 20;
				str1 = "gauss";
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((el1 == null) ? 0 : el1.hashCode());
			result = prime * result + ((map1 == null) ? 0 : map1.hashCode());
			return result;
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
			ElObjTest other = (ElObjTest) obj;
			if (el1 == null) {
				if (other.el1 != null) {
					return false;
				}
			} else if (!el1.equals(other.el1)) {
				return false;
			}
			if (map1 == null) {
				if (other.map1 != null) {
					return false;
				}
			} else if (!map1.equals(other.map1)) {
				return false;
			}
			return true;
		}
	}
	
	private static class ElArrTest extends AbstractElement {
		
		@SmofArray(name = "arr1", type = SmofType.NUMBER)
		private final List<Integer> arr1;
		
		@SmofArray(name = "arr2", type = SmofType.DATETIME)
		private final List<LocalDate> arr2;
		
		@SmofBuilder
		public ElArrTest(@SmofParam(name="arr1")List<Integer> dates1, @SmofParam(name="arr2") List<LocalDate> dates2) {
			this.arr1 = dates1;
			this.arr2 = dates2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((arr1 == null) ? 0 : arr1.hashCode());
			result = prime * result + ((arr2 == null) ? 0 : arr2.hashCode());
			return result;
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
			ElArrTest other = (ElArrTest) obj;
			if (arr1 == null) {
				if (other.arr1 != null) {
					return false;
				}
			} else if (!arr1.equals(other.arr1)) {
				return false;
			}
			if (arr2 == null) {
				if (other.arr2 != null) {
					return false;
				}
			} else if (!arr2.equals(other.arr2)) {
				return false;
			}
			return true;
		}
	}

}
