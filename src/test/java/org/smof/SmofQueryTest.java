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
import static org.smof.TestUtils.createTestConnection;
import static org.smof.dataModel.StaticDB.ALL_GUITARS;
import static org.smof.dataModel.StaticDB.BRANDS;
import static org.smof.dataModel.StaticDB.GUITARS;
import static org.smof.dataModel.StaticDB.GUITAR_1;
import static org.smof.dataModel.StaticDB.GUITAR_2;
import static org.smof.dataModel.StaticDB.GUITAR_3;
import static org.smof.dataModel.StaticDB.MODELS;
import static org.smof.dataModel.StaticDB.OWNERS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.collection.Smof;
import org.smof.collection.SmofResults;
import org.smof.dataModel.Brand;
import org.smof.dataModel.Guitar;
import org.smof.dataModel.Model;
import org.smof.dataModel.Owner;
import org.smof.dataModel.TypeGuitar;

@SuppressWarnings("javadoc")
public class SmofQueryTest {

	private static Smof smof;

	@BeforeClass
	public static void setUpBeforeClass() {
		smof = createTestConnection();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		smof.close();
	}

	@Before
	public void setUp() {
		smof.loadCollection(GUITARS, Guitar.class);
		smof.loadCollection(BRANDS, Brand.class);
		smof.loadCollection(MODELS, Model.class);
		smof.loadCollection(OWNERS, Owner.class);

		ALL_GUITARS.forEach(g -> smof.insert(g));
	}

	@After
	public void tearDown() {
		smof.dropAllCollections();
		smof.dropAllBuckets();
	}

	@Test
	public final void testAndQuery() {
		final SmofResults<Guitar> query = smof.find(Guitar.class)
				.beginAnd()
				.withFieldEquals(Guitar.PRICE, GUITAR_3.getPrice())
				.withFieldEquals(Guitar.TYPE, TypeGuitar.ACOUSTIC)
				.end().results();

		List<Guitar> result = query.stream().collect(Collectors.toList());
		assertEquals(1, result.size());
		assertTrue(result.contains(GUITAR_3));
	}

	@Test
	public final void testOrQuery() {
		final SmofResults<Guitar> query = smof.find(Guitar.class)
				.beginOr()
				.withFieldEquals(Guitar.TYPE, TypeGuitar.ELECTRIC)
				.withFieldEquals(Guitar.TYPE, TypeGuitar.CLASSIC)
				.end().results();
		List<Guitar> result = query.stream().collect(Collectors.toList());

		assertEquals(2, result.size());
		assertTrue(result.containsAll(Arrays.asList(GUITAR_1, GUITAR_2)));
	}

	@Test
	public final void testWithFieldNotIn() {
		final SmofResults<Guitar> query = smof.find(Guitar.class)
				.withFieldNotIn(Guitar.TYPE, new Object[]{TypeGuitar.ACOUSTIC})
				.results();
		List<Guitar> result = query.stream().collect(Collectors.toList());

		assertEquals(2, result.size());
		assertTrue(result.containsAll(Arrays.asList(GUITAR_1, GUITAR_2)));
	}

	@Test
	public final void testWithFieldRegex() {
		final SmofResults<Guitar> query = smof.find(Guitar.class)
				.withFieldIn(Guitar.TYPE, new Object[]{TypeGuitar.ELECTRIC, TypeGuitar.ACOUSTIC})
				.results();
		List<Guitar> result = query.stream().collect(Collectors.toList());

		assertEquals(2, result.size());
		assertTrue(result.containsAll(Arrays.asList(GUITAR_1, GUITAR_3)));
	}

}
