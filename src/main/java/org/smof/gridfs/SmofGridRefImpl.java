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

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.smof.element.AbstractElement;

import com.google.common.base.Preconditions;

class SmofGridRefImpl extends AbstractElement implements SmofGridRef {

	private static final long serialVersionUID = 1L;
	
	private Path attachedFile;
	private ByteArrayInputStream byteStream;
	private String bucketName;
	private Document metadata;
	
	SmofGridRefImpl(ObjectId id, String bucketName) {
		super(id, true);
		this.bucketName = bucketName;
		this.metadata = new Document();
	}
	
	@Override
	public void attachFile(Path path) {
		Preconditions.checkArgument(byteStream != null ^ path != null, "A SmofGridRef cannot be attached with both a file and a byte array");
		attachedFile = path;
	}

	@Override
	public Path getAttachedFile() {
		return attachedFile;
	}

	@Override
	public String getBucketName() {
		return bucketName;
	}

	@Override
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	@Override
	public void putMetadataEntry(String key, Object value) {
		metadata.put(key, value);
	}

	@Override
	public void putMetadata(Document document) {
		metadata = document;
	}

	@Override
	public Document getMetadata() {
		return metadata;
	}

	@Override
	public void attachByteArray(ByteArrayInputStream byteStream) {
		Preconditions.checkArgument(byteStream != null ^ attachedFile != null, "A SmofGridRef cannot be attached with both a file and a byte array");
		this.byteStream = byteStream;
	}

	@Override
	public boolean isEmpty() {
		return attachedFile == null && byteStream == null;
	}

	@Override
	public ByteArrayInputStream getAttachedByteArray() {
		return byteStream;
	}

	@Override
	public LocalDateTime getStorageTime() {
		return getId() == null ? null : super.getStorageTime();
	}
}
