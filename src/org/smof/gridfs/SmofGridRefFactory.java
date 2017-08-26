package org.smof.gridfs;

import java.io.File;
import java.nio.file.Path;

import org.bson.types.ObjectId;

import com.google.common.base.Preconditions;

@SuppressWarnings("javadoc")
public class SmofGridRefFactory {
	
	private SmofGridRefFactory() {}
	
	public static SmofGridRef newFromPath(Path path) {
		SmofGridRef ref = new SmofGridRefImpl(null, null);
		ref.attachFile(path);
		return ref;
	}
	
	public static SmofGridRef newFromFile(File file) {
		return newFromPath(file.toPath());
	}
	
	public static SmofGridRef newEmptyRef() {
		return new SmofGridRefImpl(null, null);
	}

	public static SmofGridRef newFromDB(ObjectId id, String bucketName) {
		Preconditions.checkArgument(id != null, "ID cannot be null");
		Preconditions.checkArgument(bucketName != null, "Bucket name cannot be null");
		return new SmofGridRefImpl(id, bucketName);
	}
	
}
