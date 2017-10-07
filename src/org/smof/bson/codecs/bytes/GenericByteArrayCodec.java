package org.smof.bson.codecs.bytes;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.BsonBinary;

class GenericByteArrayCodec extends AbstractBytesCodec<Byte[]> {

	@Override
	public Class<Byte[]> getEncoderClass() {
		return Byte[].class;
	}

	@Override
	protected BsonBinary encode(Byte[] value) {
		return new BsonBinary(ArrayUtils.toPrimitive(value));
	}

	@Override
	protected Byte[] decode(BsonBinary value) {
		return ArrayUtils.toObject(value.getData());
	}

}
