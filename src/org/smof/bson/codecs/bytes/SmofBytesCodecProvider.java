package org.smof.bson.codecs.bytes;

import java.util.Collection;
import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class SmofBytesCodecProvider implements SmofCodecProvider {

	private final Map<Class<?>, Codec<?>> codecs;
	
	public SmofBytesCodecProvider() {
		codecs = Maps.newLinkedHashMap();
		put(new PrimitiveByteArrayCodec());
		put(new GenericByteArrayCodec());
	}
	
	private void put(Codec<?> codec) {
		codecs.put(codec.getEncoderClass(), codec);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(Collection.class.isAssignableFrom(clazz)) {
			return (Codec<T>) new ByteCollectionCodec((Class<Collection<Byte>>) clazz);
		}
		return (Codec<T>) codecs.get(clazz);
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return codecs.containsKey(clazz) || Collection.class.isAssignableFrom(clazz);
	}

}
