package org.smof.bson.codecs.date;

import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class SmofDateCodecProvider implements SmofCodecProvider {

	private final Map<Class<?>, Codec<?>> codecs;
	
	public SmofDateCodecProvider() {
		codecs = Maps.newLinkedHashMap();
		put(new JavaInstantCodec());
		put(new JavaLocalDateCodec());
		put(new JavaLocalDateTimeCodec());
		put(new JavaZonedDateTimeCodec());
	}
	
	private <T> void put(Codec<T> codec) {
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
