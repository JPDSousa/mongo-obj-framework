package org.smof.codecs.date;

import static org.smof.codecs.date.JavaTimeCodecHelper.*;

import java.time.LocalDate;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

class JavaLocalDateCodec extends AbstractTimeCodec<LocalDate> {

	@Override
	public void encode(BsonWriter writer, LocalDate value, EncoderContext encoderContext) {
		writer.writeDateTime(fromLocalDate(value));
	}

	@Override
	public Class<LocalDate> getEncoderClass() {
		return LocalDate.class;
	}

	@Override
	protected LocalDate decode(long value) {
		return toLocalDate(value);
	}

}
