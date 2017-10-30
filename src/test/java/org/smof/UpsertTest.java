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
import static org.smof.TestUtils.*;
import static org.smof.dataModel.StaticDB.*;

import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.collection.CollectionOptions;
import org.smof.collection.Smof;
import org.smof.dataModel.Brand;
import org.smof.dataModel.Guitar;
import org.smof.dataModel.Location;
import org.smof.dataModel.Model;
import org.smof.dataModel.Owner;
import org.smof.dataModel.TypeGuitar;
import org.smof.gridfs.SmofGridStreamManager;

@SuppressWarnings("javadoc")
public class UpsertTest {

	private static Smof connection;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		connection = createTestConnection();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		connection.close();
	}
	
	@After
	public final void tearDown() {
		connection.dropAllBuckets();
		connection.dropAllCollections();
	}
	
	private final void createCollections(boolean upsertGuitar, boolean upsertModel, boolean upsertBrand) {
		final CollectionOptions<Guitar> guitarOpts = CollectionOptions.create();
		final CollectionOptions<Model> modelOpts = CollectionOptions.create();
		final CollectionOptions<Brand> brandOpts = CollectionOptions.create();
		final CollectionOptions<Owner> ownerOpts = CollectionOptions.create();
		guitarOpts.upsert(upsertGuitar);
		modelOpts.upsert(upsertModel);
		brandOpts.upsert(upsertBrand);
		ownerOpts.upsert(true);
		connection.createCollection(GUITARS, Guitar.class, guitarOpts);
		connection.createCollection(MODELS, Model.class, modelOpts);
		connection.createCollection(OWNERS, Owner.class, ownerOpts);
		connection.createCollection(BRANDS, Brand.class, brandOpts);
		connection.loadBucket(GUITARS_PIC_BUCKET);
	}
	
	@Test
	public void testUpsert() {
		createCollections(true, true, true);
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
		connection.replace(Brand.class, brand);
		assertEquals(brand, connection.find(Brand.class).byElement(brand));
	}
	
	@Test
	public final void testPreInsertNoUpsert() {
		final Guitar g1 = GUITAR_2;
		final Guitar g2 = GUITAR_3;
		assertEquals(g1.getModel(), g2.getModel());
		createCollections(true, false, true);
		assertTrue(connection.insert(g1));
		assertFalse(connection.insert(g2));
		assertEquals(1, connection.find(Guitar.class).results().count());
	}
	
	//@Test
	public final void testReferencesWithFalseUpsert() {
		//Insert two models referring the same brand
		//As upsert is set two false, second brand insertion fails
		//Check the reference of the second model
		createCollections(true, true, false);
		final Brand brand = BRAND_1;
		final Model model1 = Model.create("Model1", "this guy", 209, brand, Collections.singletonList("Red"));
		final Model model2 = Model.create("Model2", "this guy", 209, brand, Collections.singletonList("Red"));
		connection.insert(model1);
		connection.insert(model2);
		final Model modelDB2 = connection.find(Model.class).byElement(model2);
		assertEquals(brand, modelDB2.getBrand());
	}
	
	@Test
	public final void testGridRefPosInsertNoUpsert() {
		createCollections(false, true, true);
		final SmofGridStreamManager gridStream = connection.getGridStreamManager();
		final Guitar guitar1 = Guitar.create(MODEL_1, TypeGuitar.ELECTRIC, 1, 1995);
		final Guitar guitar2 = Guitar.create(MODEL_1, TypeGuitar.ELECTRIC, 1, 1995);
		guitar1.setPicture(TestUtils.RECOURCES_EL_GUITAR);
		guitar2.setPicture(RECOURCES_AC_GUITAR);
		connection.insert(guitar1);
		connection.insert(guitar2);
		assertEquals(1, gridStream.stream(GUITARS_PIC_BUCKET).count());
	}

}
