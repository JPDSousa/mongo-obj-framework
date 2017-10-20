package org.smof.bson.codecs.object;

import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;
import org.smof.collection.SmofDispatcher;
import org.smof.element.Element;
import org.smof.field.SmofField;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class ObjectCodecProvider implements SmofCodecProvider {
	
	private final LazyLoader lazyLoader;
	private final ObjectCodecContext encoderContext;
	
	public ObjectCodecProvider(SmofDispatcher dispatcher) {
		lazyLoader = LazyLoader.create(dispatcher);
		encoderContext = ObjectCodecContext.create();
	}
	
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		return get(clazz, registry, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> type, CodecRegistry registry, SmofField field) {
		if(field == null) {
			//returns the default codec
			return new ObjectCodec<>();
		}
		if(Element.class.isAssignableFrom(type)) {
			return (Codec<T>) createElementReferenceCodec((Class<Element>) type, registry);
		}
		return null;
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return !clazz.isPrimitive() && !clazz.isArray()	&& !clazz.isEnum();
	}
	
	private <T extends Element> ReferenceElementCodec<T> createElementReferenceCodec(Class<T> type, CodecRegistry registry) {
		return new ReferenceElementCodec<>(type, registry, lazyLoader);
	}

}
