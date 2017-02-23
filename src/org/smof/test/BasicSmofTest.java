package org.smof.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;
import org.smof.collection.Smof;
import org.smof.element.AbstractElement;
import org.smof.parsers.SmofType;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;

@SuppressWarnings("javadoc")
public class BasicSmofTest {
	
	private static Smof smof;
	private static MongoClient client;

	@BeforeClass
	public static void setUpBeforeClass() {
		final MongoDatabase database;
		client = new MongoClient("localhost", 27017);
		database = client.getDatabase("test");
		smof = Smof.create(database);
		smof.loadCollection("guitars", Guitar.class);
		smof.registerSmofObject(Brand.class);
	}

	@AfterClass
	public static void tearDownAfterClass() {
		client.close();
	}

	@Test
	public void testSingleInsert() {
		final Guitar g1 = new Guitar("GR400", Type.ELECTRIC, Tunnings.DROPD.tunning);
		final Guitar g2 = new Guitar("Manhattan", Type.ACOUSTIC, Tunnings.STANDARD.tunning);
		final Guitar g3 = new Guitar("Roxy", Type.ACOUSTIC, Tunnings.DROPC.tunning);
		smof.insert(g1);
		smof.insert(g2);
		smof.insert(g3);
	}
	
	@Test(expected = MongoWriteException.class)
	public void testDuplicateKey() {
		final Guitar g1 = new Guitar("GR4001", Type.ELECTRIC, Tunnings.DROPD.tunning);
		smof.insert(g1);
		smof.insert(g1);
	}
	
	@Test
	public void testDrop() {
		final String name = "drop";
		smof.createCollection(name, ToDrop.class);
		smof.dropCollection(name);
	}
	
	private static enum Type {
		CLASSIC,
		ACOUSTIC,
		ELECTRIC;
	}
	
	private static enum Tunnings {
		STANDARD("E-A-D-G-B-E"),
		DROPD("D-A-D-G-B-E"),
		DROPC("C-A-D-G-B-E");
		
		private final List<String> tunning;
		
		private Tunnings(String strings) {
			tunning = Arrays.asList(strings.split("-"));
		}
	}
	
	private static class Guitar extends AbstractElement {
		
		@SmofObject(name = "brand")
		private Brand brand;
		
		@SmofString(name = "model")
		private final String model;
		
		@SmofString(name = "type")
		private final Type type;
		
		@SmofArray(name = "tunning", type = SmofType.STRING)
		private final List<String> tunning;

		@SmofBuilder
		public Guitar(@SmofParam(name="model") String model, @SmofParam(name="type") Type type, @SmofParam(name="tunning") List<String> tunning) {
			super();
			this.model = model;
			this.type = type;
			this.tunning = tunning;
		}
		
	}
	
	private static class Brand {

		@SmofNumber(name="year")
		private final int year;
		
		@SmofString(name="founder")
		private final String founder;
		
		@SmofNumber(name="units")
		private final int units;
		
		@SmofBuilder
		public Brand(@SmofParam(name="year") Integer year, @SmofParam(name="founder") String founder, @SmofParam(name="units") Integer units) {
			this.year = year;
			this.founder = founder;
			this.units = units;
		}
	}
	
	private static class ToDrop extends AbstractElement {
		@SmofBuilder
		public ToDrop() {
			// TODO Auto-generated constructor stub
		}
	}

}
