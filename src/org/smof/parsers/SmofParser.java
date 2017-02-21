package org.smof.parsers;

import org.bson.BsonDocument;
import org.bson.BsonValue;

import org.smof.annnotations.SmofField;
import org.smof.element.Element;
import org.smof.exception.InvalidBsonTypeException;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.InvalidTypeException;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public class SmofParser {
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private final SmofTypeContext context;
	private final SmofParserPool parsers;
	
	public SmofParser() {
		this.context = new SmofTypeContext();
		parsers = SmofParserPool.create(this);
	}
	
	SmofTypeContext getContext() {
		return context;
	}
	
	public <T> AnnotationParser<T> getMetadata(Class<T> type) {
		return context.getMetadata(type);
	}
	
	public <T> void registerType(Class<T> type) {
		try {
			final AnnotationParser<T> parser = new AnnotationParser<>(type);
			registerType(parser);
		} catch (InvalidSmofTypeException e) {
			handleError(e);
		}
	}
	
	public <T> void registerType(Class<T> type, Object factory){
		try {
			final AnnotationParser<T> parser = new AnnotationParser<>(type, factory);
			registerType(parser);
		} catch (InvalidSmofTypeException e) {
			handleError(e);
		}
	}
	
	private void registerType(AnnotationParser<?> parser) {
		validateParserFields(parser);
		context.put(parser);
	}
	
	private void validateParserFields(AnnotationParser<?> parser) {
		for(SmofField field : parser.getAllFields()) {
			checkValidType(field);
		}
	}
	
	private void checkValidType(SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		if(!parser.isValidType(field)) {
			handleError(new InvalidTypeException(field.getFieldClass(), field.getType()));
		}
	}

	public <T extends Element> T fromBson(BsonDocument document, Class<T> type) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		
		return parser.fromBson(document, type, null);
	}
	
	Object fromBson(BsonValue value, SmofField field) {
		checkValidBson(value, field);
		final BsonParser parser = parsers.get(field.getType());
		final Class<?> type = field.getRawField().getType();
		
		return parser.fromBson(value, type, field);
	}
	
	Object fromBson(BsonValue value, Class<?> type, SmofType smofType) {
		final BsonParser parser = parsers.get(smofType);
		return parser.fromBson(value, type, null);
	}
	
	public BsonDocument toBson(Element value) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		return (BsonDocument) parser.toBson(value, null);
	}

	BsonValue toBson(Object value, SmofField field) {
		final SmofType type = field.getType();
		final BsonParser parser = parsers.get(type);
		return parser.toBson(value, field);
	}
	
	BsonValue toBson(Object value, SmofType type) {
		final BsonParser parser = parsers.get(type);
		return parser.toBson(value, null);
	}

	private void checkValidBson(BsonValue value, SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		if(!parser.isValidBson(value)) {
			handleError(new InvalidBsonTypeException(value.getBsonType(), field.getName()));
		}
	}

	boolean isValidType(SmofType smofType, Class<?> type) {
		final BsonParser parser = parsers.get(smofType);
		return parser.isValidType(type);
	}
}
