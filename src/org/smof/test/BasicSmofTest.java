package org.smof.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexes;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;
import org.smof.collection.Smof;
import org.smof.element.AbstractElement;
import org.smof.index.IndexType;
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
	}

	@AfterClass
	public static void tearDownAfterClass() {
		client.close();
	}
	
	@Before
	public final void setUp() {
		smof.createCollection("guitars", Guitar.class);
		smof.createCollection("brands", Brand.class);
	}
	
	@After
	public final void tearDown() {
		smof.dropCollection("guitars");
		smof.dropCollection("brands");
	}

	@Test
	public void testSingleInsert() {
		final Brand brand = new Brand(1965, "William Shakespear", 4000);
		final List<Guitar> guitars = new ArrayList<>();
		guitars.add(new Guitar(brand, "GR400", Type.ELECTRIC, Tunnings.DROPD.tunning));
		guitars.add(new Guitar(brand, "Manhattan", Type.ACOUSTIC, Tunnings.STANDARD.tunning));
		guitars.add(new Guitar(brand, "Roxy", Type.ACOUSTIC, Tunnings.DROPC.tunning));
		
		for(Guitar g : guitars) {
			smof.insert(g);
		}
	}
	
	@Test
	public void testQueryAll() {
		final Brand brand = new Brand(1965, "William Shakespear", 4000);
		final Map<ObjectId, Guitar> guitars = new LinkedHashMap<>();
		final Guitar g1 = new Guitar(brand, "GR400", Type.ELECTRIC, Tunnings.DROPD.tunning); 
		final Guitar g2 = new Guitar(brand, "Manhattan", Type.ACOUSTIC, Tunnings.STANDARD.tunning);
		final Guitar g3 = new Guitar(brand, "Roxy", Type.ACOUSTIC, Tunnings.DROPC.tunning);
		guitars.put(g1.getId(), g1);
		guitars.put(g2.getId(), g2);
		guitars.put(g3.getId(), g3);
		
		for(Guitar g : guitars.values()) {
			smof.insert(g);
		}
		final List<Guitar> results = smof.find(Guitar.class).results().asList();
		for(Guitar g : results) {
			assertEquals(g, guitars.get(g.getId()));
		}
	}

	@Test(expected = MongoWriteException.class)
	public void testDuplicateKey() {
		final Brand brand = new Brand(1965, "William Shakespear", 4000);
		final Guitar g1 = new Guitar(brand, "GR4001", Type.ELECTRIC, Tunnings.DROPD.tunning);
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

	@SmofIndexes({
		@SmofIndex(key = "main", unique = true)
	})
	private static class Guitar extends AbstractElement {

		@SmofObject(name = "brand")
		private final Brand brand;

		@SmofString(name = "model", indexKey = "main", indexType = IndexType.TEXT)
		private final String model;

		@SmofString(name = "type", indexKey = "main", indexType = IndexType.TEXT)
		private final Type type;

		@SmofArray(name = "tunning", type = SmofType.STRING)
		private final List<String> tunning;

		@SmofBuilder
		public Guitar(@SmofParam(name = "brand") Brand brand, 
				@SmofParam(name="model") String model, 
				@SmofParam(name="type") Type type, 
				@SmofParam(name="tunning") List<String> tunning) {
			super();
			this.model = model;
			this.type = type;
			this.tunning = tunning;
			this.brand = brand;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((brand == null) ? 0 : brand.hashCode());
			result = prime * result + ((model == null) ? 0 : model.hashCode());
			result = prime * result + ((tunning == null) ? 0 : tunning.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
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
			Guitar other = (Guitar) obj;
			if (brand == null) {
				if (other.brand != null) {
					return false;
				}
			} else if (!brand.equals(other.brand)) {
				return false;
			}
			if (model == null) {
				if (other.model != null) {
					return false;
				}
			} else if (!model.equals(other.model)) {
				return false;
			}
			if (tunning == null) {
				if (other.tunning != null) {
					return false;
				}
			} else if (!tunning.equals(other.tunning)) {
				return false;
			}
			if (type != other.type) {
				return false;
			}
			return true;
		}
	}

	private static class Brand extends AbstractElement{

		@SmofNumber(name="year")
		private final int year;

		@SmofString(name="founder")
		private final String founder;

		@SmofNumber(name="units")
		private final int units;

		@SmofBuilder
		public Brand(@SmofParam(name="year") Integer year, 
				@SmofParam(name="founder") String founder, 
				@SmofParam(name="units") Integer units) {
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
