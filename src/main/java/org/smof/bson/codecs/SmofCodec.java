package org.smof.bson.codecs;

import org.bson.BsonWriter;
import org.bson.codecs.Codec;

@SuppressWarnings("javadoc")
public interface SmofCodec<T> extends Codec<T> {
	
	void encode(BsonWriter writer, T value, SmofEncoderContext context);
	
	

}
