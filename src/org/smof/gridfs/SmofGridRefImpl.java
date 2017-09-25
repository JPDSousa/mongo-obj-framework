package org.smof.gridfs;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.smof.element.AbstractElement;

import com.google.common.base.Preconditions;

class SmofGridRefImpl extends AbstractElement implements SmofGridRef {

	private Path attachedFile;
	private ByteArrayInputStream byteStream;
	private String bucketName;
	private Document metadata;
	
	SmofGridRefImpl(ObjectId id, String bucketName) {
		super(id);
		this.bucketName = bucketName;
		this.metadata = new Document();
	}
	
	@Override
	public void attachFile(Path path) {
		Preconditions.checkArgument(byteStream != null ^ attachedFile != null, "A SmofGridRef cannot be attached with both a file and a byte array");
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

}
