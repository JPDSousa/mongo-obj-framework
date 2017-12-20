package org.smof.bson.codecs.object;

import java.util.Map;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.ObjectIdCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.smof.bson.codecs.SmofCodec;
import org.smof.element.Element;
import org.smof.gridfs.SmofGridRef;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class SmofObjectCodecProvider implements CodecProvider {

	private final LazyLoader lazyLoader;
	private final SmofParser topParser;
	
	private final SmofCodec<Map<String, Object>> mapCodec;
	private final SmofCodec<Document> docCodec;
	private final Codec<ObjectId> objIdCodec;
	private final Codec<SmofGridRef> gridRefCodec;

	public SmofObjectCodecProvider(SmofParser topParser) {
		lazyLoader = LazyLoader.create(topParser.getDispatcher());
		this.topParser = topParser;
		this.mapCodec = new MapCodec(topParser);
		this.docCodec = new DocumentCodec(mapCodec);
		this.objIdCodec = new ObjectIdCodec();
		this.gridRefCodec = new SmofGridRefCodec(topParser);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(SmofGridRef.class.isAssignableFrom(clazz)) {
			return (Codec<T>) gridRefCodec;
		}
		else if(Element.class.isAssignableFrom(clazz)) {
			return (Codec<T>) createElementReferenceCodec((Class<? extends Element>) clazz);
		}
		else if(Map.class.isAssignableFrom(clazz)) {
			// TODO check if Map<String, Object>
			return (Codec<T>) mapCodec;
		}
		else if(Document.class.equals(clazz)) {
			return (Codec<T>) docCodec;
		}
		else if(ObjectId.class.isAssignableFrom(clazz)) {
			return (Codec<T>) objIdCodec;
		}
		// returns the default codec
		return new ObjectCodec<>(clazz, topParser);
	}

	private <T extends Element> ReferenceElementCodec<T> createElementReferenceCodec(Class<T> type) {
		final Codec<T> objCodec = new ObjectCodec<>(type, topParser);
		return new ReferenceElementCodec<>(type, topParser, lazyLoader, objCodec);
	}

}
