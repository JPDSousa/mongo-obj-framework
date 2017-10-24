package org.smof.bson.codecs.object;

import org.bson.codecs.Codec;
import org.bson.codecs.ObjectIdCodec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.smof.bson.codecs.SmofCodecProvider;
import org.smof.element.Element;
import org.smof.field.MasterField;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class ObjectCodecProvider implements SmofCodecProvider {
	
	private final LazyLoader lazyLoader;
	private final ObjectCodecContext encoderContext;
	private final SmofParser topParser;
	
	public ObjectCodecProvider(SmofParser topParser) {
		lazyLoader = LazyLoader.create(topParser.getDispatcher());
		encoderContext = ObjectCodecContext.create();
		this.topParser = topParser;
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
			return new ObjectCodec<T>(type, encoderContext, topParser);
		}
		else if(field instanceof MasterField) {
			return (Codec<T>) createMasterElementCodec((Class<? extends Element>) type, registry);
		}
		else if(Element.class.isAssignableFrom(type)) {
			if(field instanceof PrimaryField) {
				return
			}
			return (Codec<T>) createElementReferenceCodec((Class<? extends Element>) type, registry);
		}
		else if(ObjectId.class.isAssignableFrom(type)) {
			return (Codec<T>) new ObjectIdCodec();
		}
		return new ObjectCodec<>(type, encoderContext, topParser);
	}

	private <T extends Element> MasterElementCodec<T> createMasterElementCodec(Class<T> type, CodecRegistry registry) {
		return new MasterElementCodec<T>(type, registry, encoderContext);
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return !clazz.isPrimitive() && !clazz.isArray() && !clazz.isEnum();
	}
	
	private <T extends Element> ReferenceElementCodec<T> createElementReferenceCodec(Class<T> type, CodecRegistry registry) {
		return new ReferenceElementCodec<>(type, registry, lazyLoader);
	}

}
