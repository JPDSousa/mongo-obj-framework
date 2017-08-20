/*******************************************************************************
 * Copyright (C) 2017 Joao
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofBoolean;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofByte;
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

import com.google.common.collect.Sets;

@SuppressWarnings("javadoc")
public class ElementTypeFactoryTest {
	
	private static SmofParser parser;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		parser = new SmofParser(null);
		
		parser.registerType(ElStrTest.class);
		parser.registerType(ElObjIdTest.class);
		parser.registerType(ElNumTest.class);
		parser.registerType(ElDateTest.class);
		parser.registerType(ElObjTest.class);
		parser.registerType(ElObjTest.ElObjTestB.class);
		parser.registerType(ElObjTest.ElObjTestA.class);
		parser.registerType(ElArrTest.class);	
		parser.registerType(ElByteTest.class);
		parser.registerType(ElBoolTest.class);
	}

	@Test
	public void testString() throws SmofException {
		final ElStrTest test = new ElStrTest("test", EnumTest.VALB);
		test.str2 = "askjdahsj";
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
		assertEquals(test, parser.fromBson(doc, ElStrTest.class));
	}
	
	@Test
	public void testObjectId() throws SmofException {
		final ElObjIdTest test = new ElObjIdTest(new ObjectId());
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
		assertEquals(test, parser.fromBson(doc, ElObjIdTest.class));
	}
	
	@Test
	public void testNumber() throws SmofException {
		final ElNumTest test = new ElNumTest(31, new Long(31), new Short((short) 200));
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
		final ElNumTest back = parser.fromBson(doc, ElNumTest.class);
		assertEquals(test, back);
		System.out.println("ID: " + back.getIdAsString());
	}
	
	@Test
	public void testDate() throws SmofException {
		final ElDateTest test = new ElDateTest(Instant.now(), LocalDate.now(), LocalDateTime.now());
		test.jodaDateTime = DateTime.now();
		test.jodaInstant = org.joda.time.Instant.now();
		test.jodaLocalDate = org.joda.time.LocalDate.now();
		test.jodaLocalDateTime = org.joda.time.LocalDateTime.now();
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
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
		System.out.println(doc.toJson());
		assertEquals(test, parser.fromBson(doc, ElObjTest.class));
	}
	
	@Test
	public void testArray() throws SmofException {
		final List<LocalDate> localDateList = Arrays.asList(LocalDate.now(), LocalDate.now());
		final ElArrTest test = new ElArrTest(Arrays.asList(500), Sets.newLinkedHashSet(localDateList));
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
		assertEquals(test, parser.fromBson(doc, ElArrTest.class));
	}
	
	@Test
	public void testByte() {
		final byte[] bytes1 = new byte[20];
		final byte[] bytes2 = new byte[200];
		final Random random = new Random();
		random.nextBytes(bytes1);
		random.nextBytes(bytes2);
		final ElByteTest test = new ElByteTest(Arrays.asList(ArrayUtils.toObject(bytes1)), Arrays.asList(ArrayUtils.toObject(bytes2)));
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
		assertEquals(test, parser.fromBson(doc, ElByteTest.class));
	}
	
	@Test
	public final void testBoolean() {
		final ElBoolTest test = new ElBoolTest(true, false);
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
		assertEquals(test, parser.fromBson(doc, ElBoolTest.class));
	}
	
	private static enum EnumTest {
		VALA,
		VALB,
		VALC;
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
		
		@SmofNumber(name = "int2")
		private Integer int2;
		
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
		
		private static final String JODA_LOCAL_DATE_TIME = "jodaLocalDateTime";
		private static final String JODA_LOCAL_DATE = "jodaLocalDate";
		private static final String JODA_DATE_TIME = "jodaDateTime";
		private static final String JODA_INSTANT = "jodaInstant";
		private static final String JAVA_LOCAL_DATE_TIME = "javaLocalDateTime";
		private static final String JAVA_LOCAL_DATE = "javaLocalDate";
		private static final String JAVA_INSTANT = "javaInstant";

		@SmofDate(name=JAVA_INSTANT)
		private final Instant javaInstant;
		
		@SmofDate(name=JAVA_LOCAL_DATE)
		private final LocalDate javaLocaldate;
		
		@SmofDate(name=JAVA_LOCAL_DATE_TIME)
		private final LocalDateTime javaLocaldateTime;
		
		@SmofDate(name=JODA_INSTANT)
		private org.joda.time.Instant jodaInstant;
		
		@SmofDate(name=JODA_DATE_TIME)
		private DateTime jodaDateTime;
		
		@SmofDate(name = JODA_LOCAL_DATE)
		private org.joda.time.LocalDate jodaLocalDate;
		
		@SmofDate(name = JODA_LOCAL_DATE_TIME)
		private org.joda.time.LocalDateTime jodaLocalDateTime;

		@SmofBuilder
		private ElDateTest(@SmofParam(name = JAVA_INSTANT) Instant date, 
				@SmofParam(name = JAVA_LOCAL_DATE) LocalDate localdate, 
				@SmofParam(name = JAVA_LOCAL_DATE_TIME) LocalDateTime localdateTime) {
			this.javaInstant = date;
			this.javaLocaldate = localdate;
			this.javaLocaldateTime = localdateTime;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((javaInstant == null) ? 0 : javaInstant.hashCode());
			result = prime * result + ((javaLocaldate == null) ? 0 : javaLocaldate.hashCode());
			result = prime * result + ((javaLocaldateTime == null) ? 0 : javaLocaldateTime.hashCode());
			result = prime * result + ((jodaDateTime == null) ? 0 : jodaDateTime.hashCode());
			result = prime * result + ((jodaInstant == null) ? 0 : jodaInstant.hashCode());
			result = prime * result + ((jodaLocalDate == null) ? 0 : jodaLocalDate.hashCode());
			result = prime * result + ((jodaLocalDateTime == null) ? 0 : jodaLocalDateTime.hashCode());
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
			ElDateTest other = (ElDateTest) obj;
			if (javaInstant == null) {
				if (other.javaInstant != null) {
					return false;
				}
			} else if (!javaInstant.equals(other.javaInstant)) {
				return false;
			}
			if (javaLocaldate == null) {
				if (other.javaLocaldate != null) {
					return false;
				}
			} else if (!javaLocaldate.equals(other.javaLocaldate)) {
				return false;
			}
			if (javaLocaldateTime == null) {
				if (other.javaLocaldateTime != null) {
					return false;
				}
			} else if (!javaLocaldateTime.equals(other.javaLocaldateTime)) {
				return false;
			}
			if (jodaDateTime == null) {
				if (other.jodaDateTime != null) {
					return false;
				}
			} else if (!jodaDateTime.equals(other.jodaDateTime)) {
				return false;
			}
			if (jodaInstant == null) {
				if (other.jodaInstant != null) {
					return false;
				}
			} else if (!jodaInstant.equals(other.jodaInstant)) {
				return false;
			}
			if (jodaLocalDate == null) {
				if (other.jodaLocalDate != null) {
					return false;
				}
			} else if (!jodaLocalDate.equals(other.jodaLocalDate)) {
				return false;
			}
			if (jodaLocalDateTime == null) {
				if (other.jodaLocalDateTime != null) {
					return false;
				}
			} else if (!jodaLocalDateTime.equals(other.jodaLocalDateTime)) {
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
		private final Set<LocalDate> arr2;
		
		@SmofBuilder
		public ElArrTest(@SmofParam(name="arr1")List<Integer> dates1, @SmofParam(name="arr2") Set<LocalDate> dates2) {
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
	
	private static class ElByteTest extends AbstractElement {
		
		private static final String BYTES2 = "bytes2";
		private static final String BYTES1 = "bytes1";

		@SmofByte(name = BYTES1)
		private final byte[] bytes1;
		
		@SmofByte(name = BYTES2)
		private final Byte[] bytes2;

		@SmofBuilder
		ElByteTest(@SmofParam(name = BYTES1) List<Byte> bytes1, 
				@SmofParam(name = BYTES2) List<Byte> bytes2) {
			super();
			this.bytes1 = ArrayUtils.toPrimitive(bytes1.toArray(new Byte[bytes1.size()]));
			this.bytes2 = bytes2.toArray(new Byte[bytes2.size()]);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(bytes1);
			result = prime * result + Arrays.hashCode(bytes2);
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
			ElByteTest other = (ElByteTest) obj;
			if (!Arrays.equals(bytes1, other.bytes1)) {
				return false;
			}
			if (!Arrays.equals(bytes2, other.bytes2)) {
				return false;
			}
			return true;
		}
	}
	
	private static class ElBoolTest extends AbstractElement {
		
		private static final String BOOL1 = "bool1";
		private static final String BOOL2 = "bool2";
		
		@SmofBoolean(name = BOOL1)
		private final Boolean bool1;
		
		@SmofBoolean(name = BOOL2)
		private final boolean bool2;
		
		@SmofBuilder
		ElBoolTest(
				@SmofParam(name = BOOL1) Boolean bool1, 
				@SmofParam(name = BOOL2) Boolean bool2) {
			super();
			this.bool1 = bool1;
			this.bool2 = bool2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((bool1 == null) ? 0 : bool1.hashCode());
			result = prime * result + (bool2 ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ElBoolTest other = (ElBoolTest) obj;
			if (bool1 == null) {
				if (other.bool1 != null) {
					return false;
				}
			} else if (!bool1.equals(other.bool1)) {
				return false;
			}
			if (bool2 != other.bool2) {
				return false;
			}
			return true;
		}
		
		
	}

}
