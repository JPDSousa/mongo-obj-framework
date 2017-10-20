package org.smof.bson.codecs.object;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.smof.element.Element;

class ElementReferenceCodec<T extends Element> implements Codec<T> {
	
	private final CodecRegistry registry;
	private final LazyLoader lazyLoader;
	private final Class<T> type;
	
	ElementReferenceCodec(Class<T> type, CodecRegistry registry, LazyLoader lazyLoader) {
		this.registry = registry;
		this.lazyLoader = lazyLoader;
		this.type = type;
	}

	@Override
	public void encode(BsonWriter writer, Element value, EncoderContext encoderContext) {
		registry.get(ObjectId.class).encode(writer, value.getId(), encoderContext);
	}

	@Override
	public Class<T> getEncoderClass() {
		return type;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final ObjectId id = registry.get(ObjectId.class).decode(reader, decoderContext);
		return lazyLoader.createLazyInstance(type, id);
	}

}
