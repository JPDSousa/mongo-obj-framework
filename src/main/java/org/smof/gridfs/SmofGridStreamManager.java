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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.smof.collection.CollectionsPool;

import com.mongodb.client.gridfs.model.GridFSFile;

@SuppressWarnings("javadoc")
public interface SmofGridStreamManager {

	static SmofGridStreamManager newStreamManager(CollectionsPool pool) {
		return new SmofGridStreamManagerImpl(pool);
	}
	
	void uploadFile(SmofGridRef ref) throws IOException;
	void uploadStream(SmofGridRef ref);
	void uploadStream(SmofGridRef ref, InputStream stream);
	
	InputStream download(SmofGridRef ref);
	void downloadToFile(SmofGridRef ref, Path path) throws IOException;
	
	void drop(SmofGridRef ref);
	
	Stream<SmofGridRef> stream(String bucketName);
	
	GridFSFile loadFileMetadata(SmofGridRef ref);
}
