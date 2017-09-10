package org.smof.test.collections;

import static org.junit.Assert.*;
import static org.smof.test.TestUtils.*;
import static org.smof.test.dataModel.StaticDB.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.collection.Smof;
import org.smof.test.dataModel.Brand;
import org.smof.test.dataModel.Guitar;
import org.smof.test.dataModel.Model;


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
	public final void testUnorderedCollectionLoading() {
		connection.loadCollection(GUITARS, Guitar.class);
		connection.loadCollection(MODELS, Model.class);
		connection.loadCollection(BRANDS, Brand.class);
		//Do collections need to be loaded according to inheritance order?
		//Consider cycling inheritance... perhaps not.
	}

}
