package org.smof.bson.codecs.date;

import java.time.Duration;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

@SuppressWarnings("javadoc")
public class JavaDurationCodec extends AbstractTimeCodec<Duration> {
	
	public static final String SECONDS = "sec";
	public static final String NANOS = "nano";

	@Override
	public void encode(BsonWriter writer, Duration value, EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeInt64(SECONDS, value.getSeconds());
		writer.writeInt32(NANOS, value.getNano());
		writer.writeEndDocument();
	}

	@Override
	public Class<Duration> getEncoderClass() {
		return Duration.class;
	}
	
	@Override
	public Duration decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonType type = reader.getCurrentBsonType();
		if(type == BsonType.DOCUMENT) {
			final long seconds;
			final int nanos;
			reader.readStartDocument();
			seconds = reader.readInt64(SECONDS);
			nanos = reader.readInt32(NANOS);
			return Duration.ofSeconds(seconds, nanos);
		}
		return super.decode(reader, decoderContext);
	}

	@Override
	protected Duration decode(long value) {
		return Duration.ofMillis(value);
	}

}
