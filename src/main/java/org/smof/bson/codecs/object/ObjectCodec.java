package org.smof.bson.codecs.object;

import java.lang.reflect.Field;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonNull;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.smof.element.Element;
import org.smof.exception.MissingRequiredFieldException;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;
import org.smof.gridfs.SmofGridRef;
import org.smof.parsers.BsonLazyObjectId;
import org.smof.parsers.SmofParser;
import org.smof.parsers.metadata.TypeBuilder;
import org.smof.parsers.metadata.TypeParser;
import org.smof.parsers.metadata.TypeStructure;
import org.smof.utils.BsonUtils;

class ObjectCodec<T> implements Codec<T> {

	private final ObjectCodecContext encoderContext;
	private final SmofParser topParser;
	private final Class<T> type;
	
	ObjectCodec(Class<T> type, ObjectCodecContext encoderContext, SmofParser topParser) {
		this.encoderContext = encoderContext;
		this.topParser = topParser;
		this.type = type;
	}

	@Override
	public void encode(BsonWriter writer, T value, org.bson.codecs.EncoderContext encoderContext) {
		final BsonValue cachedValue = this.encoderContext.getBsonValue(value);
		if(cachedValue != null) {
			writeBsonValue(writer, cachedValue);
		}
		else {
			final BsonDocument encodeObject = encodeObject(value);
			this.encoderContext.put(value, encodeObject);
			writer.pipe(new BsonDocumentReader(encodeObject));
		}
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
	
	private Object extractValue(Object element, PrimaryField field) {
		try {
			final Field rawField = field.getRawField();
			final Object value;
			rawField.setAccessible(true);
			value = rawField.get(element);
			rawField.setAccessible(false);
			return value;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void checkRequired(PrimaryField field, Object value) {
		if(field.isRequired() && value == null) {
			final String name = field.getName();
			throw new RuntimeException(new MissingRequiredFieldException(name));
		}
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

	private void setElementMetadata(BsonDocument document, Object obj) {
		if(obj instanceof Element) {
			((Element) obj).setId(document.getObjectId(Element.ID).getValue());
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
