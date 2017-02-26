package org.smof.test;

import static org.junit.Assert.*;

import org.bson.BsonDocument;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;
import org.smof.element.AbstractElement;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class ObjectInheritanceTest {

	private static SmofParser parser;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		parser = new SmofParser(null);
		parser.registerType(Ia.class, new IaFactory());
	}

	@Test
	public void testInterface() {
		final Ia test = new A(30l, 31, "32");
		final BsonDocument doc = parser.toBson(test);
		System.out.println(doc.toJson());
		assertEquals(test, parser.fromBson(doc, Ia.class));
	}
	
	private static interface Ia extends Element {
		long getNum1();
		int getNum2();
		String getStr1();
	}
	
	private static class IaFactory {
		
		private IaFactory() {}
		
		@SmofBuilder
		private Ia createIa(@SmofParam(name="num1") Long num1, 
				@SmofParam(name="num2") Integer num2, 
				@SmofParam(name="str1") String str1) {
			return new A(num1, num2, str1);
		}
	}
	
	private static class A extends AbstractElement implements Ia {
		
		@SmofNumber(name="num1")
		private final long num1;
		@SmofNumber(name="num2")
		private final int num2;
		@SmofString(name="str1")
		private final String str1;

		@SmofBuilder
		private A(@SmofParam(name="num1") Long num1, 
				@SmofParam(name="num2") Integer num2, 
				@SmofParam(name="str1") String str1) {
			super();
			this.num1 = num1;
			this.num2 = num2;
			this.str1 = str1;
		}

		@Override
		public long getNum1() {
			return num1;
		}

		@Override
		public int getNum2() {
			return num2;
		}

		@Override
		public String getStr1() {
			return str1;
		}
		
	}

}
