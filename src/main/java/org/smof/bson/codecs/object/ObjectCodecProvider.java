package org.smof.bson.codecs.object;

import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;
import org.smof.collection.SmofDispatcher;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class ObjectCodecProvider implements SmofCodecProvider {
	
	private final Map<Class<?>, Codec<?>> codecs;
	private final LazyLoader lazyLoader;
	
	public ObjectCodecProvider(SmofDispatcher dispatcher) {
		codecs = Maps.newLinkedHashMap();
		lazyLoader = LazyLoader.create(dispatcher);
	}
	
	private <T> void put(Codec<T> codec) {
		codecs.put(codec.getEncoderClass(), codec);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(Element.class.isAssignableFrom(clazz)) {
			return (Codec<T>) createElementReferenceCodec((Class<Element>) clazz, registry);
		}
		return null;
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return Element.class.isAssignableFrom(clazz)
				|| codecs.containsKey(clazz);
	}
	
	private <T extends Element> ElementReferenceCodec<T> createElementReferenceCodec(Class<T> type, CodecRegistry registry) {
		return new ElementReferenceCodec<>(type, registry, lazyLoader);
	}

}
