package org.smof.bson.codecs.array;

import java.util.Collection;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("javadoc")
public class SmofArrayCodecProvider implements CodecProvider {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(clazz.isArray()) {
			return new ArrayCodec(clazz, innerCodec);
		}
		else if(Collection.class.isAssignableFrom(clazz)) {
			return new CollectionCodec(innerCodec, clazz);
		}
		return null;
	}

}
