package org.smof.bson.codecs.bool;

import static java.lang.String.format;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

abstract class AbstractBooleanCodec<T> implements Codec<T> {

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		writer.writeBoolean(encode(value));
	}

	protected abstract boolean encode(T value);

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonType type = reader.getCurrentBsonType();
		switch(type) {
		case INT32:
			return decode(reader.readInt32() != 0);
		case NULL:
			return null;
		case STRING:
			return decode(Boolean.parseBoolean(reader.readString()));
		case BOOLEAN:
			return decode(reader.readBoolean());
			//$CASES-OMITTED$
		default:
			final String errMsg = format("Invalid date type, found: %s", type);
			throw new BsonInvalidOperationException(errMsg); 
		}
	}
	
	protected abstract T decode(boolean value);

}
