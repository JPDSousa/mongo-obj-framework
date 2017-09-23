package org.smof.test.gridfs;

import static org.junit.Assert.*;
import static org.smof.test.TestUtils.*;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.collection.Smof;
import org.smof.gridfs.SmofGridRef;
import org.smof.gridfs.SmofGridRefFactory;
import org.smof.gridfs.SmofGridStreamManager;
import org.smof.test.TestUtils;

import com.mongodb.MongoGridFSException;

import java.io.IOException;
import java.nio.file.Files;

@SuppressWarnings("javadoc")
public class SmofGridStreamManagerTest {

	private static final String BUCKET = "streamManagerTestBucket";
	private static Smof smof;
	private static SmofGridStreamManager streamManager;
	private static SmofGridRef ref;
	private static byte[] content;
	
	@BeforeClass
	public static final void setUp() throws IOException {
		smof = TestUtils.createTestConnection();
		streamManager = smof.getGridStreamManager();
		smof.loadBucket(BUCKET);
		ref = SmofGridRefFactory.newFromPath(RESOURCES_1MB);
		ref.setBucketName(BUCKET);
		content = Files.readAllBytes(RESOURCES_1MB);
	}
	
	@AfterClass
	public static final void drop() {
		smof.dropBucket(BUCKET);
		smof.close();
	}
	
	@Test
	public final void testUploadFile() throws IOException {
		streamManager.uploadFile(ref);
		final byte[] actual = IOUtils.toByteArray(streamManager.download(ref));
		assertEquals(content.length, actual.length);
		assertArrayEquals(content, actual);
		streamManager.drop(ref);
	}
	
	@Test
	public final void testUploadStream() throws IOException {
		streamManager.uploadStream(ref, Files.newInputStream(RESOURCES_1MB));
		final byte[] actual = IOUtils.toByteArray(streamManager.download(ref));
		assertEquals(content.length, actual.length);
		assertArrayEquals(content, actual);
		streamManager.drop(ref);
	}
	
	@Test(expected = MongoGridFSException.class)
	public final void testDrop() throws IOException {
		streamManager.uploadFile(ref);
		streamManager.drop(ref);
		streamManager.download(ref);
	}

}
