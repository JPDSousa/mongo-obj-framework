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
			final Codec<?> innerCodec = getInnerCodec(clazz, registry);
			return new ArrayCodec(clazz, innerCodec);
		}
		else if(Collection.class.isAssignableFrom(clazz)) {
			final Codec<?> innerCodec = getInnerCodec(clazz, registry);
			return new CollectionCodec(innerCodec, clazz);
		}
		return null;
	}

	private <T> Codec<?> getInnerCodec(Class<T> clazz, CodecRegistry registry) {
		final TypeToken<T> type = TypeToken.of(clazz);
		final TypeToken<?> componentType = type.resolveType(Collection.class.getTypeParameters()[0]);
		final Codec<?> innerCodec = registry.get(componentType.getRawType());
		return innerCodec;
	}

}
