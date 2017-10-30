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
package org.smof.collection;

import static org.junit.Assert.*;
import static org.smof.TestUtils.*;
import static org.smof.dataModel.StaticDB.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.dataModel.Brand;
import org.smof.dataModel.Guitar;
import org.smof.dataModel.Model;


@SuppressWarnings("javadoc")
public class CollectionTest {

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
	
	@Test
	public final void testDuplicateCollectionLoading() {
		connection.loadCollection(BRANDS, Brand.class);
		connection.loadCollection(BRANDS, Brand.class);
		//A collection can be loaded multiple times
		//Collection loading is idempotent ;)
	}
	
	@Test
	public final void testGetCollection() {
		connection.loadCollection(BRANDS, Brand.class);
		assertNotNull(connection.getCollection(Brand.class));
	}
	
	@Test
	public final void testUnorderedCollectionLoading() {
		connection.loadCollection(GUITARS, Guitar.class);
		connection.loadCollection(MODELS, Model.class);
		connection.loadCollection(BRANDS, Brand.class);
		//Do collections need to be loaded according to inheritance order?
		//Consider cycling inheritance... perhaps not.
	}

}
