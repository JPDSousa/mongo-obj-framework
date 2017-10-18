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
package org.smof.gridfs;

import static org.junit.Assert.*;
import static org.smof.TestUtils.*;

import org.bson.types.ObjectId;
import org.junit.Test;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SuppressWarnings("javadoc")
public class SmofGridRefTest {

	private static final String BUCKET_1 = "bucket1";
	private static final Path TEST_PATH = RESOURCES_4MB;
	
	@Test
	public final void testCreate() {
		assertNotNull(SmofGridRefFactory.newFromPath(TEST_PATH));
		assertNotNull(SmofGridRefFactory.newFromFile(TEST_PATH.toFile()));
		assertNotNull(SmofGridRefFactory.newFromDB(new ObjectId(), BUCKET_1));
		assertNotNull(SmofGridRefFactory.newEmptyRef());
	}
	
	@Test
	public final void testNewFromDB() {
		final ObjectId expectedID = new ObjectId();
		final String expectedBucket = BUCKET_1;
		final SmofGridRef ref = SmofGridRefFactory.newFromDB(expectedID, expectedBucket);
		assertEquals(expectedID, ref.getId());
		assertEquals(expectedBucket, ref.getBucketName());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void testNewFromDBNull() {
		SmofGridRefFactory.newFromDB(new ObjectId(), null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void testNewFromDBNull1() {
		SmofGridRefFactory.newFromDB(null, BUCKET_1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void testNewFromDBNull2() {
		SmofGridRefFactory.newFromDB(null, null);
	}
	
	@Test
	public final void testAttachedFile() {
		final SmofGridRef ref = SmofGridRefFactory.newFromPath(TEST_PATH);
		assertEquals(TEST_PATH, ref.getAttachedFile());
	}
	
	@Test
	public final void testSetAttachedFile() {
		final SmofGridRef ref = SmofGridRefFactory.newEmptyRef();
		ref.attachFile(TEST_PATH);
		assertEquals(TEST_PATH, ref.getAttachedFile());
	}
	
	@Test
	public final void testStorageTime() {
		final ObjectId id = new ObjectId();
		final LocalDateTime expected = LocalDateTime.ofInstant(id.getDate().toInstant(), ZoneId.systemDefault());
		final SmofGridRef ref = SmofGridRefFactory.newEmptyRef();
		assertNull(ref.getStorageTime());
		ref.setId(id);
		assertEquals(expected, ref.getStorageTime());
	}
	
	@Test
	public final void testSetID() {
		final SmofGridRef ref = SmofGridRefFactory.newEmptyRef();
		final ObjectId expectedID = new ObjectId();
		ref.setId(expectedID);
		assertEquals(expectedID, ref.getId());
	}

}
