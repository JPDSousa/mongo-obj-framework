/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
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
package org.smof;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.bson.BsonDocument;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexes;
import org.smof.collection.Smof;
import org.smof.dataModel.Brand;
import org.smof.dataModel.Guitar;
import org.smof.dataModel.Location;
import org.smof.dataModel.Model;
import org.smof.dataModel.Owner;
import org.smof.dataModel.TypeGuitar;
import org.smof.element.AbstractElement;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.gridfs.SmofGridStreamManager;
import org.smof.index.InternalIndex;

import static org.smof.TestUtils.*;
import static org.smof.dataModel.StaticDB.*;

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
		smof.createCollection(OWNERS, Owner.class);
		smof.loadBucket(GUITARS_PIC_BUCKET);
	}
	
	@After
	public final void tearDown() {
		smof.dropAllBuckets();
		smof.dropAllCollections();
	}
	
	@Test
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
		final Brand brand = Brand.create("Gibson", new Location("Here", "Now"), Collections.singletonList(OWNER_1));
		smof.insert(brand);
		smof.insert(brand);
		final long count = smof.find(Brand.class).results().count();
		assertEquals(1, count);
	}
	
	@Test
	public void testQueryAll() {
		for(Guitar g : ALL_GUITARS) {
			assertTrue(smof.insert(g));
		}
		final List<Guitar> results = smof.find(Guitar.class).results().asList();
		for(Guitar g : results) {
			assertTrue(ALL_GUITARS.contains(g));
		}
	}
	
	@Test
	public void testUpdateReplace() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
		smof.insert(brand);
		brand.setCapital(1000);
		smof.update(Brand.class).fromElement(brand);
		assertEquals(brand, smof.find(Brand.class).byElement(brand));
	}
	
	@Test
	public void testUpdateIncrease() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
		final long inc = 75L;
		smof.insert(brand);
		smof.update(Brand.class)
		.increase(inc, Brand.CAPITAL)
		.where()
		.fieldEq(Brand.NAME, "Gibson")
		.execute();
		brand.increaseCapital(inc);
		final Brand actual = smof.find(Brand.class).byElement(brand);
		assertEquals(brand, actual);
	}

	@Test(expected = SmofException.class)
	public void testUpdateUnknownField() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
		smof.insert(brand);
		smof.update(Brand.class)
		.where()
		.fieldEq("unknown", "Gibson")
		.execute();
	}

	@Test
	public void testUpdateMultiply() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
		brand.setCapital(4L);
		final long mul = 2L;
		smof.insert(brand);
		smof.update(Brand.class)
		.multiply(mul, Brand.CAPITAL)
		.where()
		.fieldEq(Brand.NAME, "Gibson")
		.execute();
		brand.multiplyCapital(mul);
		final Brand actual = smof.find(Brand.class).byElement(brand);
		assertEquals(brand, actual);
	}

	@Test
	public void testUpdateSet() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
		smof.insert(brand);
		Location newLocation = new Location("New York", "USA");
		smof.update(Brand.class)
		.set(newLocation, Brand.LOCATION)
		.where()
		.fieldEq(Brand.NAME, "Gibson")
		.execute();
		final Brand expected = Brand.create("Gibson", newLocation, Collections.singletonList(OWNER_1));
		final Brand actual = smof.find(Brand.class).byElement(brand);
		assertEquals(expected, actual);
	}

	@Test
	public void testUpdateSetSameFieldTwice() {
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
		smof.insert(brand);
		Location newLocation1 = new Location("New York", "USA");
		Location newLocation2 = new Location("Los-Angeles", "USA");
		smof.update(Brand.class)
		.set(newLocation1, Brand.LOCATION)
		.set(newLocation2, Brand.LOCATION)
		.where()
		.fieldEq(Brand.NAME, "Gibson")
		.execute();
		final Brand expected = Brand.create("Gibson", newLocation2, Collections.singletonList(OWNER_1));
		final Brand actual = smof.find(Brand.class).byElement(brand);
		assertEquals(expected, actual);
	}

	@Test
	public void testSmofGridRef() throws IOException {
		final SmofGridStreamManager gridStream = smof.getGridStreamManager();
		final byte[] guitar1Pic = Files.readAllBytes(RECOURCES_EL_GUITAR);
		final byte[] guitar2Pic = Files.readAllBytes(RECOURCES_AC_GUITAR);
		final Guitar guitar1 = Guitar.create(MODEL_1, TypeGuitar.ELECTRIC, 1, 1995);
		final Guitar guitar2 = Guitar.create(MODEL_2, TypeGuitar.ACOUSTIC, 1, 1965);
		guitar1.setPicture(TestUtils.RECOURCES_EL_GUITAR);
		guitar2.setPicture(RECOURCES_AC_GUITAR);
		smof.insert(guitar1);
		smof.insert(guitar2);
		final byte[] actualEl = IOUtils.toByteArray(gridStream.download(guitar1.getPicture()));
		final byte[] actualAc = IOUtils.toByteArray(gridStream.download(guitar2.getPicture()));
		assertArrayEquals(guitar1Pic, actualEl);
		assertArrayEquals(guitar2Pic, actualAc);
	}

	@Test
	public final void testDrop() {
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
