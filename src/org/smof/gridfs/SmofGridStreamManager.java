package org.smof.gridfs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.smof.collection.CollectionsPool;

import com.mongodb.client.gridfs.model.GridFSFile;

@SuppressWarnings("javadoc")
public interface SmofGridStreamManager {

	public static SmofGridStreamManager newStreamManager(CollectionsPool pool) {
		return new SmofGridStreamManagerImpl(pool);
	}
	
	void uploadFile(SmofGridRef ref) throws IOException;
	void uploadStream(SmofGridRef ref, InputStream stream);
	
	InputStream download(SmofGridRef ref);
	public void downloadToFile(SmofGridRef ref, Path path) throws IOException;
	
	void drop(SmofGridRef ref);
	
	Stream<SmofGridRef> stream(String bucketName);
	
	GridFSFile loadFileMetadata(SmofGridRef ref);
}
