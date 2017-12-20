package org.smof.bson.codecs;

import org.bson.codecs.CollectibleCodec;

@SuppressWarnings("javadoc")
public interface SmofCollectibleCodec<T> extends CollectibleCodec<T>, SmofCodec<T> {
	
	// no additional methods required
	
}
