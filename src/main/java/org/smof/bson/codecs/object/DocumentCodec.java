package org.smof.bson.codecs.object;

import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.smof.bson.codecs.SmofCodec;
import org.smof.bson.codecs.SmofEncoderContext;

class DocumentCodec implements SmofCodec<Document> {
	
	private final SmofCodec<Map<String, Object>> innerCodec;
	
	DocumentCodec(SmofCodec<Map<String, Object>> innerCodec) {
		super();
		this.innerCodec = innerCodec;
	}

	@Override
	public void encode(BsonWriter writer, Document value, EncoderContext encoderContext) {
		innerCodec.encode(writer, value, encoderContext);
	}

	@Override
	public Class<Document> getEncoderClass() {
		return Document.class;
	}

	@Override
	public Document decode(BsonReader reader, DecoderContext decoderContext) {
		return new Document(innerCodec.decode(reader, decoderContext));
	}

	@Override
	public void encode(BsonWriter writer, Document value, SmofEncoderContext context) {
		innerCodec.encode(writer, value, context);
	}

	@Override
	public Document decode(BsonReader reader, SmofEncoderContext context) {
		return new Document(innerCodec.decode(reader, context));
	}

}
