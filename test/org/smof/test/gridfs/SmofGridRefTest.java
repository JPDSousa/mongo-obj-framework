package org.smof.test.gridfs;

import static org.junit.Assert.*;
import static org.smof.test.TestUtils.*;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.smof.gridfs.SmofGridRef;
import org.smof.gridfs.SmofGridRefFactory;

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
