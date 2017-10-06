package org.smof.bson.codecs;

import static java.lang.String.format;

import org.bson.BsonBinary;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

abstract class AbstractBytesCodec<T> implements Codec<T> {

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		writer.writeBinaryData(encode(value));
	}
	
	protected abstract BsonBinary encode(T value);

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonType type = reader.getCurrentBsonType();
		if(type == BsonType.BINARY) {
			return decode(reader.readBinaryData());
		}
		final String errMsg = format("Invalid date type, found: %s", type);
		throw new BsonInvalidOperationException(errMsg); 
	}
	
	protected abstract T decode(BsonBinary value);

}
