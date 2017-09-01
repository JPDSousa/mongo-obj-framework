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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import static org.smof.test.TestUtils.*;
import static org.smof.test.dataModel.StaticDB.*;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@SuppressWarnings("javadoc")
public class BasicSmofTest {
	
	private static Smof smof;
	private static MongoClient client;
	private static MongoDatabase database;
	
	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void setUpBeforeClass() {
		client = new MongoClient(TEST_HOST, TEST_PORT);
		database = client.getDatabase(TEST_DB);
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
		for(Guitar g : ALL_GUITARS) {
			smof.insert(g);
		}
	}
	
	@Test
	public void testRedundantInsert() {
		final Brand brand = Brand.create("Gibson", new Location("Here", "Now"), Arrays.asList("This guy"));
		smof.insert(brand);
		smof.insert(brand);
		final long count = smof.find(Brand.class).results().count();
		assertEquals(1, count);
	}
	
	//@Test
	public void testQueryAll() {
		final Map<ObjectId, Guitar> guitars = ALL_GUITARS.stream().collect(
				Collectors.groupingBy(Guitar::getId, 
						Collectors.reducing(null, (g1, g2) -> g1)));
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
