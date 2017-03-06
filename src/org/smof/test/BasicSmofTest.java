package org.smof.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexes;
import org.smof.collection.Smof;
import org.smof.element.AbstractElement;
import org.smof.element.Element;
import org.smof.index.InternalIndex;
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

	private static final String MODELS = "models";
	private static final String BRANDS = "brands";
	private static final String GUITARS = "guitars";
	
	private static Smof smof;
	private static MongoClient client;
	private static MongoDatabase database;
	
	@BeforeClass
	public static void setUpBeforeClass() {
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
		smof.createCollection(GUITARS, Guitar.class);
		smof.createCollection(BRANDS, Brand.class);
		smof.createCollection(MODELS, Model.class);
	}
	
	@After
	public final void tearDown() {
		smof.dropCollection(GUITARS);
		smof.dropCollection(BRANDS);
		smof.dropCollection(MODELS);
	}
	
	public void testIndexUpdating() {
		final Set<InternalIndex> before = new LinkedHashSet<>();
		final Set<InternalIndex> after = new LinkedHashSet<>();
		for(BsonDocument doc : database.getCollection(GUITARS).listIndexes(BsonDocument.class)) {
			before.add(InternalIndex.fromBson(doc));
		}
		smof.loadCollection(GUITARS, Guitar.class);
		for(BsonDocument doc : database.getCollection(GUITARS).listIndexes(BsonDocument.class)) {
			after.add(InternalIndex.fromBson(doc));
		}
		assertEquals(before, after);
	}
	
	@Test
	public void testInternalIndexEquals() {
		final InternalIndex i1 = InternalIndex.fromBson(database.getCollection(GUITARS).listIndexes(BsonDocument.class).first());
		final InternalIndex i2 = InternalIndex.fromBson(database.getCollection(GUITARS).listIndexes(BsonDocument.class).first());
		assertEquals(i1, i2);
	}
	
	@Test
	public void testInternalIndex() {
		final Set<InternalIndex> mongoIndexes = new LinkedHashSet<>();
		final Set<InternalIndex> noteIndexes = new LinkedHashSet<>();
		for(BsonDocument doc : database.getCollection(GUITARS).listIndexes(BsonDocument.class)) {
			mongoIndexes.add(InternalIndex.fromBson(doc));
		}
		for(BsonDocument doc : database.getCollection(MODELS).listIndexes(BsonDocument.class)) {
			mongoIndexes.add(InternalIndex.fromBson(doc));
		}
		for(BsonDocument doc : database.getCollection(BRANDS).listIndexes(BsonDocument.class)) {
			mongoIndexes.add(InternalIndex.fromBson(doc));
		}
		removeId(mongoIndexes);
		for(SmofIndex index : Guitar.class.getAnnotation(SmofIndexes.class).value()) {
			noteIndexes.add(InternalIndex.fromSmofIndex(index));
		}
		for(SmofIndex index : Model.class.getAnnotation(SmofIndexes.class).value()) {
			noteIndexes.add(InternalIndex.fromSmofIndex(index));
		}
		for(SmofIndex index : Brand.class.getAnnotation(SmofIndexes.class).value()) {
			noteIndexes.add(InternalIndex.fromSmofIndex(index));
		}
		assertEquals(mongoIndexes, noteIndexes);
	}

	private void removeId(Set<InternalIndex> indexes) {
		for(InternalIndex index : indexes) {
			if(((BsonDocument) index.getIndex()).containsKey(Element.ID)) {
				indexes.remove(index);
				return;
			}
		}
	}

	@Test
	public void testSingleInsert() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("Me", "Myself", "I"));
		final Model model1 = Model.create("Manhattan", "Tyler", 1000, brand, Arrays.asList("red", "blue"));
		final Model model2 = Model.create("BeeGees", "Tyler", 5463, brand, Arrays.asList("sunburst", "ebony"));
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
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("You"));
		final Model model1 = Model.create("Manhattan", "Tyler", 1000, brand, Arrays.asList("red", "blue"));
		final Model model2 = Model.create("BeeGees", "Tyler", 5463, brand, Arrays.asList("sunburst", "ebony"));
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
	
	@Test
	public void testUpdateReplace() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("You"));
		smof.insert(brand);
		brand.setCapital(1000);
		smof.update(Brand.class).fromElement(brand);
		assertEquals(brand, smof.find(Brand.class).byElement(brand));
	}
	
	@Test
	public void testUpdateIncrease() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("You"));
		final long inc = 75l;
		smof.insert(brand);
		smof.update(Brand.class)
		.increase(inc, Brand.CAPITAL)
		.where()
		.fieldEq(Brand.NAME, "Gibson")
		.execute();
		brand.increaseCapital(inc);
		assertEquals(brand, smof.find(Brand.class).byElement(brand));
	}
	
	@Test
	public void testUpsert() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("You"));
		smof.update(Brand.class)
			.setUpsert(true)
			.fromElement(brand);
		assertEquals(brand, smof.find(Brand.class).byElement(brand));
	}

	@Test(expected = MongoWriteException.class)
	public void testDuplicateKey() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("You"));
		final Model model2 = Model.create("BeeGees", "Tyler", 5463, brand, Arrays.asList("sunburst", "ebony"));
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
