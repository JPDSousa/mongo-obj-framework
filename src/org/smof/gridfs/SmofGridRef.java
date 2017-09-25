package org.smof.gridfs;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import org.bson.Document;
import org.smof.element.Element;

@SuppressWarnings("javadoc")
public interface SmofGridRef extends Element {
	
	public String getBucketName();
	public void setBucketName(String bucketName);
	
	public void attachFile(Path path);
	public Path getAttachedFile();
	
	public void attachByteArray(ByteArrayInputStream byteStream);
	public ByteArrayInputStream getAttachedByteArray();
	
	public void putMetadataEntry(String key, Object value);
	public void putMetadata(Document document);
	public Document getMetadata();
	
	public boolean isEmpty();

}
