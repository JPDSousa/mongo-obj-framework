package org.smof.codecs.date;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

import static org.smof.codecs.date.JavaTimeCodecHelper.*;

import java.time.ZonedDateTime;

@SuppressWarnings("javadoc")
public class JavaZonedDateTimeCodec extends AbstractTimeCodec<ZonedDateTime> {

	@Override
	public void encode(BsonWriter writer, ZonedDateTime value, EncoderContext encoderContext) {
		writer.writeDateTime(fromZonedDateTime(value));
	}

	@Override
	public Class<ZonedDateTime> getEncoderClass() {
		return ZonedDateTime.class;
	}

	@Override
	protected ZonedDateTime decode(long value) {
		return toZonedDateTime(value);
	}

}
