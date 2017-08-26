package org.smof.gridfs;

import java.nio.file.Path;

import org.bson.types.ObjectId;

class SmofGridRefImpl implements SmofGridRef {

	private Path attachedFile;
	private String bucketName;
	private ObjectId id;
	
	SmofGridRefImpl(ObjectId id, String bucketName) {
		this.id = id;
		this.bucketName = bucketName;
	}
	
	@Override
	public void attachFile(Path path) {
		attachedFile = path;
	}

	@Override
	public Path getAttachedFile() {
		return attachedFile;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public String getBucketName() {
		return bucketName;
	}

	@Override
	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

}
