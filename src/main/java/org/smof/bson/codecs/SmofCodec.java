package org.smof.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;

@SuppressWarnings("javadoc")
public interface SmofCodec<T> extends Codec<T> {
	
	EncoderContext DUMMY_CONTEXT = EncoderContext.builder().build();
	
	void encode(BsonWriter writer, T value, SmofEncoderContext context);
	
	T decode(BsonReader reader, SmofEncoderContext context);

}
