package org.smof.bson.codecs.date;

import java.time.Duration;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

@SuppressWarnings("javadoc")
public class JavaDurationCodec extends AbstractTimeCodec<Duration> {

	@Override
	public void encode(BsonWriter writer, Duration value, EncoderContext encoderContext) {
		writer.writeDateTime(value.toMillis());
	}

	@Override
	public Class<Duration> getEncoderClass() {
		return Duration.class;
	}

	@Override
	protected Duration decode(long value) {
		return Duration.ofMillis(value);
	}

}
