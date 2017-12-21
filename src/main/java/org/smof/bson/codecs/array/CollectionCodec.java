package org.smof.bson.codecs.array;

import java.util.Collection;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.smof.bson.codecs.SmofCodec;
import org.smof.bson.codecs.SmofEncoderContext;
import org.smof.utils.CollectionUtils;

class CollectionCodec<T> implements SmofCodec<Collection<T>> {

	private final Codec<T> codec;
	private final Class<Collection<T>> encoderClass;
	
	CollectionCodec(Codec<T> codec, Class<Collection<T>> encoderClass) {
		super();
		this.codec = codec;
		this.encoderClass = encoderClass;
	}

	@Override
	public void encode(BsonWriter writer, Collection<T> value, EncoderContext encoderContext) {
		writer.writeStartArray();
		for(T item : value) {
			codec.encode(writer, item, encoderContext);
		}
		writer.writeEndArray();
	}

	@Override
	public Class<Collection<T>> getEncoderClass() {
		return encoderClass;
	}

	@Override
	public Collection<T> decode(BsonReader reader, DecoderContext decoderContext) {
		final Collection<T> collection = CollectionUtils.create(encoderClass);
		reader.readStartArray();
		while(reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			collection.add(codec.decode(reader, decoderContext));
		}
		reader.readEndArray();
		return collection;
	}

	@Override
	public void encode(BsonWriter writer, Collection<T> value, SmofEncoderContext context) {
		encode(writer, value, DUMMY_CONTEXT);
	}

	@Override
	public Collection<T> decode(BsonReader reader, SmofEncoderContext context) {
		return decode(reader, DE_DUMMY_CONTEXT);
	}

}
