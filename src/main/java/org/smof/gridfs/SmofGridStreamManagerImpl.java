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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.smof.collection.CollectionsPool;
import org.smof.element.Element;

import com.google.common.base.Preconditions;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;

class SmofGridStreamManagerImpl implements SmofGridStreamManager {

	private final CollectionsPool pool;
	
	SmofGridStreamManagerImpl(CollectionsPool pool) {
		this.pool = pool;
	}

	@Override
	public void uploadFile(SmofGridRef ref) throws IOException {
		final Path attachedFile = ref.getAttachedFile();
		Preconditions.checkNotNull(attachedFile, "No file attached");
		final String fileName = attachedFile.getFileName().toString();
		final InputStream stream = Files.newInputStream(attachedFile);
		uploadStream(ref, fileName, stream);
	}

	@Override
	public void uploadStream(SmofGridRef ref) {
		final ByteArrayInputStream byteStream = ref.getAttachedByteArray();
		Preconditions.checkNotNull(byteStream, "No byte stream attached");
		uploadStream(ref, byteStream);
	}

	@Override
	public void uploadStream(SmofGridRef ref, InputStream stream) {
		uploadStream(ref, RandomStringUtils.random(8), stream);
	}
	
	private void uploadStream(SmofGridRef ref, String name, InputStream stream) {
		final String bucketName = ref.getBucketName();
		final ObjectId id;
		final GridFSBucket bucket;
		Preconditions.checkNotNull(bucketName, "No bucket specified");
		final GridFSUploadOptions options = new GridFSUploadOptions().metadata(ref.getMetadata());
		bucket = pool.getBucket(bucketName);
		id = bucket.uploadFromStream(name, stream, options);
		ref.setId(id);
	}

	@Override
	public InputStream download(SmofGridRef ref) {
		final String bucketName = ref.getBucketName();
		final ObjectId id = ref.getId();
		Preconditions.checkArgument(id != null, "No download source found");
		Preconditions.checkArgument(bucketName != null, "No bucket specified");
		final GridFSBucket bucket = pool.getBucket(bucketName);
		return bucket.openDownloadStream(id);
	}

	@Override
	public void downloadToFile(SmofGridRef ref, Path path) throws IOException {
		Files.copy(download(ref), path);
	}

	@Override
	public void drop(SmofGridRef ref) {
		final String bucketName = ref.getBucketName();
		final ObjectId id = ref.getId();
		Preconditions.checkArgument(id != null, "No download source found");
		Preconditions.checkArgument(bucketName != null, "No bucket specified");
		final GridFSBucket bucket = pool.getBucket(bucketName);
		bucket.delete(id);
	}

	@Override
	public Stream<SmofGridRef> stream(String bucketName) {
		return StreamSupport.stream(pool.getBucket(bucketName).find().spliterator(), false)
				.map(file -> SmofGridRefFactory.newFromDB(file.getId().asObjectId().getValue(), bucketName));
	}
	
	@Override
	public GridFSFile loadFileMetadata(SmofGridRef ref) {
		final GridFSBucket bucket = pool.getBucket(ref.getBucketName());
		return bucket.find(Filters.eq(Element.ID, ref.getId())).first();
	}

}
