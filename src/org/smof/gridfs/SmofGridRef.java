package org.smof.gridfs;

import java.nio.file.Path;

import org.bson.types.ObjectId;

@SuppressWarnings("javadoc")
public interface SmofGridRef {
	
	public ObjectId getId();
	public void setId(ObjectId id);
	
	public String getBucketName();
	public void setBucketName(String bucketName);
	
	public void attachFile(Path path);
	public Path getAttachedFile();

}
