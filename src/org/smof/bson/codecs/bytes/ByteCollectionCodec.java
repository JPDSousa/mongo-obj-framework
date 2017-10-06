package org.smof.bson.codecs.bytes;

import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.BsonBinary;
import org.bson.BsonInvalidOperationException;
import org.smof.utils.CollectionUtils;

class ByteCollectionCodec extends AbstractBytesCodec<Collection<Byte>> {

	private final Class<Collection<Byte>> encoderClass;
	
	public ByteCollectionCodec(Class<Collection<Byte>> encoderClass) {
		super();
		this.encoderClass = encoderClass;
	}

	@Override
	public Class<Collection<Byte>> getEncoderClass() {
		return encoderClass;
	}

	@Override
	protected BsonBinary encode(Collection<Byte> value) {
		final Byte[] data = value.toArray(new Byte[value.size()]);
		return new BsonBinary(ArrayUtils.toPrimitive(data));
	}

	@Override
	protected Collection<Byte> decode(BsonBinary bsonValue) {
		final Collection<Byte> value = getInstance();
		for(byte bsonByte : bsonValue.getData()) {
			value.add(bsonByte);
		}
		return value;
	}

	private Collection<Byte> getInstance() {
		try {
			return CollectionUtils.create(encoderClass);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
				| SecurityException e) {
			throw new BsonInvalidOperationException(e.getMessage());
		}
	}

}
