package org.smof.gridfs;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.bson.Document;
import org.bson.types.ObjectId;

class SmofGridRefImpl implements SmofGridRef {

	private Path attachedFile;
	private String bucketName;
	private ObjectId id;
	private Document metadata;
	
	SmofGridRefImpl(ObjectId id, String bucketName) {
		this.id = id;
		this.bucketName = bucketName;
		this.metadata = new Document();
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
	public String getIdAsString() {
		return id.toHexString();
	}

	@Override
	public void setId(ObjectId id) {
		this.id = id;
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
	public LocalDateTime getStorageTime() {
		if(id != null) {
			return LocalDateTime.ofInstant(id.getDate().toInstant(), ZoneId.systemDefault());
		}
		return null;
	}

}
