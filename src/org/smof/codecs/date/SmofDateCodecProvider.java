package org.smof.codecs.date;

import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class SmofDateCodecProvider implements CodecProvider {

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

}
