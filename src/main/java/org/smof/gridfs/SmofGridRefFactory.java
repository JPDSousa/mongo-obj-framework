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

import java.io.File;
import java.nio.file.Path;

import org.bson.types.ObjectId;

import com.google.common.base.Preconditions;

@SuppressWarnings("javadoc")
public class SmofGridRefFactory {
	
	private SmofGridRefFactory() {}
	
	public static SmofGridRef newFromPath(Path path) {
		SmofGridRef ref = new SmofGridRefImpl(null, null);
		ref.attachFile(path);
		return ref;
	}
	
	public static SmofGridRef newFromFile(File file) {
		return newFromPath(file.toPath());
	}
	
	public static SmofGridRef newEmptyRef() {
		return new SmofGridRefImpl(null, null);
	}

	public static SmofGridRef newFromDB(ObjectId id, String bucketName) {
		Preconditions.checkArgument(id != null, "ID cannot be null");
		Preconditions.checkArgument(bucketName != null, "Bucket name cannot be null");
		return new SmofGridRefImpl(id, bucketName);
	}
	
}
