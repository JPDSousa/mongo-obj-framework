package org.smof.test;

import static org.junit.Assert.*;

import java.time.Instant;

import org.bson.BsonDocument;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofDate;
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
		assertEquals(test, parser.fromBson(doc, Ia.class));
	}
	
	@Test
	public void testNonRegType() {
		final B test = new B("work", Instant.now());
		final BsonDocument doc = parser.toBson(test);
		assertEquals(test, parser.fromBson(doc, B.class));
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

		private A(Long num1, Integer num2, String str1) {
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (num1 ^ (num1 >>> 32));
			result = prime * result + num2;
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
			A other = (A) obj;
			if (num1 != other.num1) {
				return false;
			}
			if (num2 != other.num2) {
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

	private static class B extends AbstractElement implements Element {
		
		@SmofString(name="str1")
		private final String str1;
		@SmofDate(name="instant")
		private final Instant instant;

		@SmofBuilder
		private B(@SmofParam(name="str1") String str1, 
				@SmofParam(name="instant") Instant instant) {
			this.str1 = str1;
			this.instant = instant;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((instant == null) ? 0 : instant.hashCode());
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
			B other = (B) obj;
			if (instant == null) {
				if (other.instant != null) {
					return false;
				}
			} else if (!instant.equals(other.instant)) {
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
}
