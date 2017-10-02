package org.smof.bson.codecs.date;

import static org.smof.bson.codecs.date.JavaTimeCodecHelper.*;

import java.time.LocalDateTime;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

class JavaLocalDateTimeCodec extends AbstractTimeCodec<LocalDateTime> {

	@Override
	public void encode(BsonWriter writer, LocalDateTime value, EncoderContext encoderContext) {
		writer.writeDateTime(fromLocalDateTime(value));
	}

	@Override
	public Class<LocalDateTime> getEncoderClass() {
		return LocalDateTime.class;
	}

	@Override
	protected LocalDateTime decode(long value) {
		return toLocalDateTime(value);
	}
	
}
