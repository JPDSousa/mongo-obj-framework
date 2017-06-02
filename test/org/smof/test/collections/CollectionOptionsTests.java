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
	public void testCreate() {
		assertNotNull(CollectionOptions.create());
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
		guineaPig.throwOnConstraintBreach(false);
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
