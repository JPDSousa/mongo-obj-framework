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

import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smof.TestUtils;
import org.smof.collection.Smof;

import com.mongodb.MongoGridFSException;
import com.mongodb.client.gridfs.model.GridFSFile;

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
	public static void setUp() throws IOException {
		smof = TestUtils.createTestConnection();
		streamManager = smof.getGridStreamManager();
		smof.loadBucket(BUCKET);
		ref = SmofGridRefFactory.newFromPath(RESOURCES_1MB);
		ref.setBucketName(BUCKET);
		content = Files.readAllBytes(RESOURCES_1MB);
	}
	
	@AfterClass
	public static void drop() {
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
	public final void testMetadata() throws IOException {
		final Document metadata = new Document("randomkey", 45);
		ref.putMetadata(metadata);
		streamManager.uploadFile(ref);
		ref.putMetadata(new Document());
		final GridFSFile file = streamManager.loadFileMetadata(ref);
		assertEquals(metadata, file.getMetadata());
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
