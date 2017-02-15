package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.element.AbstractElement;
import org.smof.element.SmofFactory;
import org.smof.element.SmofAnnotationParser;
import org.smof.element.SmofAdapterPool;
import org.smof.element.SmofAdapter;
import org.smof.element.field.SmofArray;
import org.smof.element.field.SmofDate;
import org.smof.element.field.SmofObject;
import org.smof.element.field.SmofNumber;
import org.smof.element.field.SmofObjectId;
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
		
		adapters.put(buildAdapter(ElStrTest.class, new ElStrTest()));
		adapters.put(buildAdapter(ElObjIdTest.class, new ElObjIdTest()));
		adapters.put(buildAdapter(ElNumTest.class, new ElNumTest()));
		adapters.put(buildAdapter(ElDateTest.class, new ElDateTest()));
		adapters.put(buildAdapter(ElObjTest.class, new ElObjTest()));
		adapters.put(buildAdapter(ElObjTest.ElObjTestB.class, new ElObjTest.ElObjTestB()));
		adapters.put(buildAdapter(ElObjTest.ElObjTestA.class, new ElObjTest.ElObjTestA()));
		adapters.put(buildAdapter(ElArrTest.class, new ElArrTest()));
		
		adapters.put(new SmofAdapter<>(new ElArrTest(), new SmofAnnotationParser<>(ElArrTest.class), adapters));
	}
	
	private static <T> SmofAdapter<T> buildAdapter(Class<T> clazz, SmofFactory<T> factory) throws InvalidSmofTypeException {
		return new SmofAdapter<T>(factory, new SmofAnnotationParser<>(clazz), adapters);
	}

	@Test
	public void testString() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElStrTest> parser = adapters.get(ElStrTest.class); 
		System.out.println(parser.write(new ElStrTest()).toJson());
	}
	
	@Test
	public void testObjectId() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElObjIdTest> parser = adapters.get(ElObjIdTest.class); 
		System.out.println(parser.write(new ElObjIdTest()).toJson());
	}
	
	@Test
	public void testNumber() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElNumTest> parser = adapters.get(ElNumTest.class); 
		System.out.println(parser.write(new ElNumTest()).toJson());
	}
	
	@Test
	public void testDate() throws SmofException, NoSuchAdapterException {
		final SmofAdapter<ElDateTest> parser = adapters.get(ElDateTest.class); 
		System.out.println(parser.write(new ElDateTest()).toJson());
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
	
	private static class ElStrTest extends AbstractElement implements SmofFactory<ElStrTest> {
		
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
		public ElStrTest createSmofObject(BsonDocument map) {
			return new ElStrTest();
		}
	}
	
	private static class ElObjIdTest extends AbstractElement implements SmofFactory<ElObjIdTest>{

		@SmofObjectId(name = "objId", ref = "coll1")
		private final ObjectId objId;
		
		public ElObjIdTest() {
			this.objId = new ObjectId();
		}

		@Override
		public ElObjIdTest createSmofObject(BsonDocument map) {
			return new ElObjIdTest();
		}
	}
	
	private static class ElNumTest extends AbstractElement implements SmofFactory<ElNumTest> {
		
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
		public ElNumTest createSmofObject(BsonDocument map) {
			return new ElNumTest();
		}
	}
	
	private static class ElDateTest extends AbstractElement implements SmofFactory<ElDateTest> {
		
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
		public ElDateTest createSmofObject(BsonDocument map) {
			return new ElDateTest();
		}
	}
	
	private static class ElObjTest extends AbstractElement implements SmofFactory<ElObjTest> {
		
		@SmofObject(name = "el1")
		private final ElObjTestA el1;
		
		@SmofObject(name = "el2")
		private final ElObjTestB el2;
		
		public ElObjTest() {
			el1 = new ElObjTestA();
			el2 = new ElObjTestB();
		}
		
		@Override
		public ElObjTest createSmofObject(BsonDocument map) {
			return new ElObjTest();
		}

		private static class ElObjTestA implements SmofFactory<ElObjTestA> {
			
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

			@Override
			public ElObjTestA createSmofObject(BsonDocument map) throws SmofException {
				return new ElObjTestA();
			}
		}
		
		private static class ElObjTestB extends AbstractElement implements SmofFactory<ElObjTestB>{

			@SmofNumber(name = "int1")
			private final int int1;
			
			@SmofString(name = "str1")
			private final String str1;
			
			public ElObjTestB() {
				int1 = 20;
				str1 = "gauss";
			}

			@Override
			public ElObjTestB createSmofObject(BsonDocument map) {
				return new ElObjTestB();
			}
		}
	}
	
	private static class ElArrTest extends AbstractElement implements SmofFactory<ElArrTest>{
		
		@SmofArray(name = "arr1", type = FieldType.NUMBER)
		private final int[] arr1;
		
		@SmofArray(name = "arr2", type = FieldType.DATE)
		private final LocalDate[] arr2;
		
		public ElArrTest() {
			this.arr1 = new int[500];
			Arrays.fill(arr1, 30);
			this.arr2 = new LocalDate[]{LocalDate.now(), LocalDate.now()};
		}

		@Override
		public ElArrTest createSmofObject(BsonDocument map) {
			return new ElArrTest();
		}
	}

}
