package org.smof.bson.codecs.bool;

import java.util.Map;

import org.bson.codecs.BooleanCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class SmofBooleanCodecProvider implements SmofCodecProvider {

	private final Map<Class<?>, Codec<?>> codecs;
	
	public SmofBooleanCodecProvider() {
		codecs = Maps.newLinkedHashMap();
		put(new BooleanCodec());
		put(new StringCodec());
		put(new IntegerCodec());
	}
	
	private void put(Codec<?> codec) {
		codecs.put(codec.getEncoderClass(), codec);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		return (Codec<T>) codecs.get(clazz);
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return codecs.containsKey(clazz);
	}

}
