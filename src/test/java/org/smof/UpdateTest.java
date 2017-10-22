package org.smof;

import static org.junit.Assert.*;
import static org.smof.dataModel.StaticDB.*;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.collection.Smof;
import org.smof.dataModel.Brand;
import org.smof.dataModel.Guitar;
import org.smof.dataModel.Location;
import org.smof.dataModel.Model;
import org.smof.dataModel.Owner;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public class UpdateTest {
	
	private static Smof smof;

	@BeforeClass
	public static void setUpBeforeClass() {
		smof = TestUtils.createTestConnection();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		smof.close();
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
	public void testUpdateIncrease() {
		final Location location = new Location("Nashville", "USA");
		final List<Owner> owners = Collections.singletonList(OWNER_1);
		final Brand brand = Brand.create("Gibson", location, owners);
		final long inc = 75L;
		smof.insert(brand);
		smof.update(Brand.class)
		.increase(Brand.CAPITAL, inc)
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
		.multiply(Brand.CAPITAL, mul)
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
		.set(Brand.LOCATION, newLocation)
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
		.set(Brand.LOCATION, newLocation1)
		.set(Brand.LOCATION, newLocation2)
		.where()
		.fieldEq(Brand.NAME, "Gibson")
		.execute();
		final Brand expected = Brand.create("Gibson", newLocation2, Collections.singletonList(OWNER_1));
		final Brand actual = smof.find(Brand.class).byElement(brand);
		assertEquals(expected, actual);
	}

}
