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
import org.smof.annnotations.SmofBuilder;
import org.smof.collection.Smof;
import org.smof.element.AbstractElement;
import org.smof.test.dataModel.Brand;
import org.smof.test.dataModel.Guitar;
import org.smof.test.dataModel.Location;
import org.smof.test.dataModel.Model;
import org.smof.test.dataModel.TypeGuitar;

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
		smof.createCollection("models", Model.class);
	}
	
	@After
	public final void tearDown() {
		smof.dropCollection("guitars");
		smof.dropCollection("brands");
		smof.dropCollection("models");
	}

	@Test
	public void testSingleInsert() {
		final Brand brand = Brand.create(new Location("Nashville", "USA"), "Me", "Myself", "I");
		final Model model1 = Model.create("Manhattan", 1000, brand, Arrays.asList("red", "blue"));
		final Model model2 = Model.create("BeeGees", 5463, brand, Arrays.asList("sunburst", "ebony"));
		final List<Guitar> guitars = new ArrayList<>();
		guitars.add(Guitar.create(model2, TypeGuitar.ELECTRIC, 1, 0));
		guitars.add(Guitar.create(model1, TypeGuitar.CLASSIC, 0, 20));
		guitars.add(Guitar.create(model1, TypeGuitar.ACOUSTIC, 0, 0));
		
		for(Guitar g : guitars) {
			smof.insert(g);
		}
	}
	
	@Test
	public void testQueryAll() {
		final Brand brand = Brand.create(new Location("Nashville", "USA"), "You");
		final Model model1 = Model.create("Manhattan", 1000, brand, Arrays.asList("red", "blue"));
		final Model model2 = Model.create("BeeGees", 5463, brand, Arrays.asList("sunburst", "ebony"));
		final Map<ObjectId, Guitar> guitars = new LinkedHashMap<>();
		final Guitar g1 = Guitar.create(model2, TypeGuitar.ELECTRIC, 1, 0); 
		final Guitar g2 = Guitar.create(model1, TypeGuitar.CLASSIC, 0, 20);
		final Guitar g3 = Guitar.create(model1, TypeGuitar.ACOUSTIC, 0, 0);
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
		final Brand brand = Brand.create(new Location("Nashville", "USA"), "You");
		final Model model2 = Model.create("BeeGees", 5463, brand, Arrays.asList("sunburst", "ebony"));
		final Guitar g1 = Guitar.create(model2, TypeGuitar.ELECTRIC, 1, 0);
		smof.insert(g1);
		smof.insert(g1);
	}

	@Test
	public void testDrop() {
		final String name = "drop";
		smof.createCollection(name, ToDrop.class);
		smof.dropCollection(name);
	}

	private static class ToDrop extends AbstractElement {
		@SmofBuilder
		public ToDrop() {
			// TODO Auto-generated constructor stub
		}
	}

}
