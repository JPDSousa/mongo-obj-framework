package org.smof.bson.codecs;

import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;

@SuppressWarnings("javadoc")
public interface SmofCollectibleCodec<T> extends CollectibleCodec<T> {

	void encode(BsonWriter writer, T value, SmofEncoderContext context);
	
}
