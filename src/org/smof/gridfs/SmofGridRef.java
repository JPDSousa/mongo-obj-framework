package org.smof.gridfs;

import java.nio.file.Path;

import org.bson.Document;
import org.bson.types.ObjectId;

@SuppressWarnings("javadoc")
public interface SmofGridRef {
	
	public ObjectId getId();
	public void setId(ObjectId id);
	
	public String getBucketName();
	public void setBucketName(String bucketName);
	
	public void attachFile(Path path);
	public Path getAttachedFile();
	
	public void putMetadataEntry(String key, Object value);
	public void putMetadata(Document document);
	public Document getMetadata();

}
