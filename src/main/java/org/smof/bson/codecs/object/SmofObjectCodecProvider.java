package org.smof.bson.codecs.object;

import org.bson.codecs.Codec;
import org.bson.codecs.ObjectIdCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.gridfs.SmofGridRef;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class SmofObjectCodecProvider implements CodecProvider {

	private final LazyLoader lazyLoader;
	private final SmofParser topParser;

	public SmofObjectCodecProvider(SmofParser topParser) {
		lazyLoader = LazyLoader.create(topParser.getDispatcher());
		this.topParser = topParser;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(SmofGridRef.class.isAssignableFrom(clazz)) {
			return (Codec<T>) new SmofGridRefCodec(topParser);
		}
		else if(Element.class.isAssignableFrom(clazz)) {
			return (Codec<T>) createElementReferenceCodec((Class<? extends Element>) clazz);
		}
		else if(ObjectId.class.isAssignableFrom(clazz)) {
			return (Codec<T>) new ObjectIdCodec();
		}
		// returns the default codec
		return new ObjectCodec<>(clazz, topParser);
	}

	private <T extends Element> ReferenceElementCodec<T> createElementReferenceCodec(Class<T> type) {
		final Codec<T> objCodec = new ObjectCodec<>(type, topParser);
		return new ReferenceElementCodec<>(type, topParser, lazyLoader, objCodec);
	}

}
