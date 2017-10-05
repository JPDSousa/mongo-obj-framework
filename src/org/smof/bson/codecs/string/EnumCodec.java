package org.smof.bson.codecs.string;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

class EnumCodec<T extends Enum<T>> implements Codec<T> {
	
	private final Class<T> clazz;

	EnumCodec(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		writer.writeString(value.name());
	}

	@Override
	public Class<T> getEncoderClass() {
		return clazz;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		try {
			return Enum.valueOf(clazz, reader.readString());
		} catch (IllegalArgumentException e) {
			throw new BsonInvalidOperationException(e.getMessage());
		}
	}

}
