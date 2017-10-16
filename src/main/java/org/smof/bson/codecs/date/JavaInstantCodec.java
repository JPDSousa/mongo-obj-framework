package org.smof.bson.codecs.date;

import static org.smof.bson.codecs.date.JavaTimeCodecHelper.*;

import java.time.Instant;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

class JavaInstantCodec extends AbstractTimeCodec<Instant> {

	@Override
	public void encode(BsonWriter writer, Instant value, EncoderContext encoderContext) {
		writer.writeDateTime(fromInstant(value));
	}

	@Override
	public Class<Instant> getEncoderClass() {
		return Instant.class;
	}

	@Override
	protected Instant decode(long value) {
		return toInstant(value);
	}

}
