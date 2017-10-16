package org.smof.bson.codecs.string;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;

@SuppressWarnings("javadoc")
public class SmofStringCodecProvider implements SmofCodecProvider {
	
	private static final Class<?>[] TYPES = {String.class, Integer.class};
	
	private final Codec<String> stringCodec;
	private final Codec<Integer> integerCodec;
	
	public SmofStringCodecProvider() {
		stringCodec = new StringCodec();
		integerCodec = new IntegerCodec();
	}

	@SuppressWarnings({ "unchecked", "cast", "rawtypes" })
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(Integer.class.equals(clazz)) {
			return (Codec<T>) integerCodec;
		}
		if(String.class.equals(clazz)) {
			return (Codec<T>) stringCodec;
		}
		if(Enum.class.isAssignableFrom(clazz)) {
			return (Codec<T>) new EnumCodec(clazz);
		}
		return null;
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return ArrayUtils.contains(TYPES, clazz) || Enum.class.isAssignableFrom(clazz);
	}

}
