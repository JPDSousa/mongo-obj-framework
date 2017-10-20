package org.smof.bson.codecs.object;

import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;

class ObjectCodec<T> implements Codec<T> {
	
	private final ObjectCodecContext encoderContext;
	
	ObjectCodec(ObjectCodecContext encoderContext) {
		this.encoderContext = encoderContext;
	}

	@Override
	public void encode(BsonWriter writer, T value, org.bson.codecs.EncoderContext encoderContext) {
		final BsonValue cachedValue = this.encoderContext.getBsonValue(value);
		if(cachedValue != null) {
			writeBsonValue(writer, cachedValue);
		}
		else {
			
		}
	}

	@Override
	public Class<T> getEncoderClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
