package org.smof.test.collections;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.smof.collection.CollectionOptions;
import org.smof.exception.SmofException;
import org.smof.test.dataModel.Brand;
import org.smof.test.dataModel.Location;

import com.google.common.collect.Lists;

@SuppressWarnings("javadoc")
public class CollectionOptionsTests {

	private static CollectionOptions<Brand> guineaPig;
	private static Brand brand;
	private static final String NAME = "name"; 
	
	@Before
	public void setUp() {
		guineaPig = CollectionOptions.create();
		brand = Brand.create(NAME, new Location("here", "not there"), Arrays.asList("owner"));
	}

	@Test
	public void testConstraints() {
		final List<Predicate<Brand>> constraints = Lists.newArrayList();
		constraints.add(b -> b.getCapital() > 0);
		constraints.add(b -> b.getFoundingDate().isBefore(LocalDate.now()));
		
		constraints.forEach(c -> guineaPig.addConstraint(c));
		assertEquals(constraints, guineaPig.getConstraints());
	}
	
	@Test
	public void testIsValid() {
		final long capital = 10;
		brand.setCapital(capital+2);
		guineaPig.addConstraint(b -> b.getCapital() > 10);
		assertTrue(guineaPig.isValid(brand));
		guineaPig.addConstraint(b -> !b.getName().equals(NAME));
		assertFalse(guineaPig.isValid(brand));
	}
	
	@Test(expected = SmofException.class)
	public void testThrowOnConstraintBreach() {
		guineaPig.addConstraint(b -> !b.getName().equals(NAME));
		guineaPig.throwOnConstraintBreach(true);
		guineaPig.isValid(brand);
	}

}
