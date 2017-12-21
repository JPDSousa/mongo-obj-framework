package org.smof.bson.codecs.array;

import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.smof.bson.codecs.SmofCodec;
import org.smof.bson.codecs.SmofEncoderContext;

import com.google.common.collect.Lists;

class ArrayCodec<T> implements SmofCodec<T[]> {
	
	private final Class<T[]> encoderClass;
	private final Codec<T> innerCodec;
	
	ArrayCodec(Class<T[]> encoderClass, Codec<T> innerCodec) {
		super();
		this.encoderClass = encoderClass;
		this.innerCodec = innerCodec;
	}

	@Override
	public void encode(BsonWriter writer, T[] value, EncoderContext encoderContext) {
		writer.writeStartArray();
		for(T item : value) {
			innerCodec.encode(writer, item, encoderContext);
		}
		writer.writeEndArray();
	}

	@Override
	public Class<T[]> getEncoderClass() {
		return encoderClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] decode(BsonReader reader, DecoderContext decoderContext) {
		final List<T> list = Lists.newArrayList();
		reader.readStartArray();
		while(reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			list.add(innerCodec.decode(reader, decoderContext));
		}
		reader.readEndArray();
		return (T[]) list.toArray();
	}

	@Override
	public void encode(BsonWriter writer, T[] value, SmofEncoderContext context) {
		encode(writer, value, DUMMY_CONTEXT);
	}

	@Override
	public T[] decode(BsonReader reader, SmofEncoderContext context) {
		return decode(reader, DE_DUMMY_CONTEXT);
	}

}
