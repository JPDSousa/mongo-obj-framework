package org.smof.bson.codecs.object;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.smof.element.Element;

class LazyReferenceElementCodec<T extends Element> implements Codec<T> {
	
	private final Class<T> type;
	
	public LazyReferenceElementCodec(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		writer.writeo
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
