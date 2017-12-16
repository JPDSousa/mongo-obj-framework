package org.smof.bson.codecs.object;

import static org.smof.bson.codecs.object.ObjectUtils.*;
import static org.smof.utils.BsonUtils.*;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonNull;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.smof.bson.codecs.SmofCodec;
import org.smof.bson.codecs.SmofEncoderContext;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;
import org.smof.gridfs.SmofGridRef;
import org.smof.parsers.BsonLazyObjectId;
import org.smof.parsers.SmofParser;
import org.smof.parsers.metadata.TypeBuilder;
import org.smof.parsers.metadata.TypeParser;
import org.smof.parsers.metadata.TypeStructure;
import org.smof.utils.BsonUtils;

class ObjectCodec<T> implements SmofCodec<T> {

	private final ParserCache parserCache;
	private final SmofParser topParser;
	private final Class<T> type;
	
	ObjectCodec(Class<T> type, SmofParser topParser) {
		this.parserCache = topParser.getCache();
		this.topParser = topParser;
		this.type = type;
	}

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		final BsonValue cachedValue = this.parserCache.getBsonValue(value);
		if(cachedValue != null) {
			writeBsonValue(writer, cachedValue);
		}
		else {
			final BsonDocument encodeObject = encodeObject(value);
			this.parserCache.put(value, encodeObject);
			writer.pipe(new BsonDocumentReader(encodeObject));
		}
	}
	
	@Override
	public void encode(BsonWriter writer, T value, SmofEncoderContext context) {
		encode(writer, value, (EncoderContext) null);
	}

	private <E> TypeStructure<E> getTypeStructure(Class<E> type) {
		return topParser.getContext().getTypeStructure(type, topParser.getParsers());
	}
	
	private <E> TypeParser<E> getTypeParser(Class<E> type) {
		return getTypeStructure(type).getParser(type);
	}

	private BsonDocument encodeObject(Object value) {
		final BsonDocument document = new BsonDocument();
		final TypeParser<?> metadata = getTypeParser(value.getClass());
		final BsonArray lazyStack = new BsonArray();

		for(PrimaryField field : metadata.getAllFields()) {
			final Object fieldValue = extractValue(value, field);
			final BsonValue parsedValue;

			checkRequired(field, fieldValue);
			parsedValue = topParser.toBson(fieldValue, field);
			if(parsedValue instanceof BsonLazyObjectId) {
				lazyStack.add(parsedValue);
			}
			else {
				document.put(field.getName(), parsedValue);
			}
		}
		document.append(SmofParser.ON_INSERT, lazyStack);
		return document;
	}
	
	private void writeBsonValue(BsonWriter writer, BsonValue value) {
		if(value.isObjectId()) {
			writer.writeObjectId(value.asObjectId().getValue());
		}
		else if(value.isDocument()) {
			writer.pipe(new BsonDocumentReader(value.asDocument()));
		}
	}

	@Override
	public Class<T> getEncoderClass() {
		return type;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonDocument document = BsonUtils.readDocument(reader);
		final BsonBuilder<T> builder = new BsonBuilder<>();
		final T obj = buildObject(document, builder);
		fillObject(document, builder, obj);
		return obj;
	}
	
	private void fillObject(BsonDocument document, final BsonBuilder<T> builder, final T obj) {
		final TypeParser<?> typeParser = getTypeParser(obj.getClass());
		for(PrimaryField field : typeParser.getNonBuilderFields()) {
			handleField(document, builder, field);
		}
		builder.fillElement(obj);
		setElementMetadata(document, obj);
	}
	
	private void handleField(BsonDocument document, BsonBuilder<T> builder, PrimaryField field) {
		final BsonValue fieldValue = document.get(field.getName());
		final Object parsedObj;
		if(fieldValue != null) {
			if(fieldValue.isObjectId() && !SmofGridRef.class.isAssignableFrom(field.getFieldClass())) {
				builder.append2LazyElements(field, fieldValue.asObjectId().getValue());
			}
			else {
				parsedObj = topParser.fromBson(fieldValue, field);
				builder.append2AdditionalFields(field.getRawField(), parsedObj);
			}
		}
	}

	private T buildObject(BsonDocument document, BsonBuilder<T> builder) {
		final TypeBuilder<T> typeBuilder = getTypeBuilder();
		for(ParameterField field : typeBuilder.getParams()) {
			final BsonValue fieldValue = document.get(field.getName(), new BsonNull());
			final Object parsedObj;
			parsedObj = topParser.fromBson(fieldValue, field);
			builder.append(field.getName(), parsedObj);
		}
		return builder.build(typeBuilder);
	}

	private final TypeBuilder<T> getTypeBuilder() {
		return getTypeStructure(type).getBuilder();
	}

}
