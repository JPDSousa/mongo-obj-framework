package org.smof.codecs.date;

import static java.lang.String.format;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;

abstract class AbstractTimeCodec<T> implements Codec<T> {
	
	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonType type = reader.getCurrentBsonType();
		switch(type) {
		case DATE_TIME:
			return decode(reader.readDateTime());
		case DOUBLE:
			return decode((long) reader.readDouble());
		case INT32:
			return decode(reader.readInt32());
		case INT64:
			return decode(reader.readInt64());
		case NULL:
			return null;
		case STRING:
			return decode(Long.valueOf(reader.readString()));
			//$CASES-OMITTED$
		default:
			final String errMsg = format("Invalid date type, found: %s", type);
			throw new BsonInvalidOperationException(errMsg); 
		}
	}
	
	protected abstract T decode(long value);

}
