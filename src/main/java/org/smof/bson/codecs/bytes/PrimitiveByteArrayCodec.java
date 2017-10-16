package org.smof.bson.codecs.bytes;

import org.bson.BsonBinary;

class PrimitiveByteArrayCodec extends AbstractBytesCodec<byte[]> {
	
	@Override
	public Class<byte[]> getEncoderClass() {
		return byte[].class;
	}

	@Override
	protected BsonBinary encode(byte[] value) {
		return new BsonBinary(value);
	}

	@Override
	protected byte[] decode(BsonBinary value) {
		return value.getData();
	}

}
