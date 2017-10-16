package org.smof.gridfs;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import org.bson.Document;
import org.smof.element.Element;

@SuppressWarnings("javadoc")
public interface SmofGridRef extends Element {
	
	String getBucketName();
	void setBucketName(String bucketName);
	
	void attachFile(Path path);
	Path getAttachedFile();
	
	void attachByteArray(ByteArrayInputStream byteStream);
	ByteArrayInputStream getAttachedByteArray();
	
	void putMetadataEntry(String key, Object value);
	void putMetadata(Document document);
	Document getMetadata();
	
	boolean isEmpty();

}
