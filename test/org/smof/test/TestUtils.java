package org.smof.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.smof.collection.Smof;

@SuppressWarnings("javadoc")
public class TestUtils {

	public static final Path RESOURCES = Paths.get("testResources");
	public static final Path RESOURCES_4MB = RESOURCES.resolve("test4mb.jpg");
	public static final Path RESOURCES_1MB = RESOURCES.resolve("test1mb.jpg");
	
	public static final String TEST_HOST = "localhost";
	public static final String TEST_DB = "test";
	public static final int TEST_PORT = 27020;
	
	public static final Smof createTestConnection() {
		return Smof.create(TEST_HOST, TEST_PORT, TEST_DB);
	}
}
