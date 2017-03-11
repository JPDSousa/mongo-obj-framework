package org.smof.parsers;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.BsonBinary;
import org.bson.BsonValue;
import org.smof.collection.SmofDispatcher;
import org.smof.field.SmofField;

class ByteParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {byte[].class, Byte[].class};
	
	protected ByteParser(SmofDispatcher dispatcher, SmofParser bsonParser) {
		super(dispatcher, bsonParser, VALID_TYPES);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		if(isPrimitiveByteArray(type)) {
			return fromPrimitiveByteArray((byte[]) value);
		}
		else if (isGenericByteArray(type)){
			return fromGenericByteArray((Byte[]) value);
		}
		return null;
	}

	private BsonBinary fromGenericByteArray(Byte[] value) {
		return new BsonBinary(ArrayUtils.toPrimitive(value));
	}

	private boolean isGenericByteArray(Class<?> type) {
		return type.equals(Byte[].class);
	}

	private BsonBinary fromPrimitiveByteArray(byte[] value) {
		return new BsonBinary(value);
	}

	private boolean isPrimitiveByteArray(Class<?> type) {
		return type.equals(byte[].class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue rawValue, Class<T> type, SmofField fieldOpts) {
		final BsonBinary value = rawValue.asBinary();
		if(isPrimitiveByteArray(type)) {
			return (T) toPrimitiveByteArray(value);
		}
		else if(isGenericByteArray(type)) {
			return (T) toGenericByteArray(value);
		}
		return null;
	}

	private Byte[] toGenericByteArray(BsonBinary value) {
		return ArrayUtils.toObject(value.getData());
	}

	private byte[] toPrimitiveByteArray(BsonBinary value) {
		return value.getData();
	}

}
