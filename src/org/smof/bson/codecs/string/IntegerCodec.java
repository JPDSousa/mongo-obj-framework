package org.smof.bson.codecs.string;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

@SuppressWarnings("javadoc")
public class IntegerCodec extends org.bson.codecs.IntegerCodec {

	@Override
	public void encode(BsonWriter writer, Integer value, EncoderContext encoderContext) {
		writer.writeString(String.valueOf(value));
	}

	@Override
	public Integer decode(BsonReader reader, DecoderContext decoderContext) {
		if(reader.getCurrentBsonType() == BsonType.STRING) {
			return Integer.valueOf(reader.readString());
		}
		return super.decode(reader, decoderContext);
	}
}
