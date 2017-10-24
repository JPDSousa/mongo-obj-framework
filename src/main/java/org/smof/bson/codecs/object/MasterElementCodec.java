package org.smof.bson.codecs.object;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.element.Element;

class MasterElementCodec<T extends Element> implements Codec<T> {
	
	private final CodecRegistry registry;
	private final ObjectCodecContext encoderContext;
	private final Class<T> type;
	
	MasterElementCodec(Class<T> type, CodecRegistry registry, ObjectCodecContext encoderContext) {
		this.registry = registry;
		this.encoderContext = encoderContext;
		this.type = type;
	}

	@Override
	public void encode(BsonWriter writer, T value, org.bson.codecs.EncoderContext encoderContext) {
		this.encoderContext.put(value, new BsonObjectId(value.getId()));
		registry.get(getEncoderClass()).encode(writer, value, encoderContext);
	}

	@Override
	public Class<T> getEncoderClass() {
		return type;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		return null;
	}

}
