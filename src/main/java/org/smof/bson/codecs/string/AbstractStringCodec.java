package org.smof.bson.codecs.string;

import static java.lang.String.format;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

abstract class AbstractStringCodec<T> implements Codec<T> {

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		writer.writeString(value.toString());
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonType type = reader.getCurrentBsonType();
		switch(type) {
		case DATE_TIME:
			return decode(Long.toString(reader.readDateTime()));
		case DOUBLE:
			return decode(Double.toString(reader.readDouble()));
		case INT32:
			return decode(Integer.toString(reader.readInt32()));
		case INT64:
			return decode(Long.toString(reader.readInt64()));
		case NULL:
			return null;
		case STRING:
			return decode(reader.readString());
		case BINARY:
			return decode(new String(reader.readBinaryData().getData()));
		case BOOLEAN:
			return decode(Boolean.toString(reader.readBoolean()));
		case JAVASCRIPT:
			return decode(reader.readJavaScript());
		case JAVASCRIPT_WITH_SCOPE:
			return decode(reader.readJavaScriptWithScope());
		case OBJECT_ID:
			return decode(reader.readObjectId().toHexString());
		case REGULAR_EXPRESSION:
			return decode(reader.readRegularExpression().getPattern());
		case SYMBOL:
			return decode(reader.readSymbol());
			//$CASES-OMITTED$
		default:
			final String errMsg = format("Invalid date type, found: %s", type);
			throw new BsonInvalidOperationException(errMsg); 
		}
	}
	
	protected abstract T decode(String value);

}
