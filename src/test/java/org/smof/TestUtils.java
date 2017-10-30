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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.smof.collection.Smof;

@SuppressWarnings("javadoc")
public class TestUtils {

	public static final Path RESOURCES = Paths.get("src", "test", "resources");
	public static final Path RESOURCES_4MB = RESOURCES.resolve("test4mb.jpg");
	public static final Path RESOURCES_1MB = RESOURCES.resolve("test1mb.jpg");
	public static final Path RECOURCES_EL_GUITAR = RESOURCES.resolve("elecGuitar.jpg");
	public static final Path RECOURCES_AC_GUITAR = RESOURCES.resolve("acGuitar.png");
	
	public static final String TEST_HOST = "localhost";
	public static final String TEST_DB = "test";
	public static final int TEST_PORT = 27020;
	
	public static Smof createTestConnection() {
		return Smof.create(TEST_HOST, TEST_PORT, TEST_DB);
	}
}
