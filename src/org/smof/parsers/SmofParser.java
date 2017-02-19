package org.smof.parsers;

import org.bson.BsonDocument;
import org.bson.BsonValue;

import org.smof.annnotations.SmofField;
import org.smof.element.Element;
import org.smof.exception.InvalidBsonTypeException;
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
	
	public SmofTypeContext getContext() {
		return context;
	}
	
	public <T extends Element> T fromBson(BsonDocument document, Class<T> type) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		
		return parser.fromBson(document, type);
	}
	
	Object fromBson(BsonValue value, SmofField field) {
		checkValidBson(value, field);
		final BsonParser parser = parsers.get(field.getType());
		final Class<?> type = field.getRawField().getType();
		
		return parser.fromBson(value, type);
	}
	
	public BsonDocument toBson(Element value) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		
		return (BsonDocument) parser.toBson(value, null);
	}

	BsonValue toBson(Object value, SmofField field) {
		checkValidType(value, field);
		return toBson(value, field.getType());
	}
	
	BsonValue toBson(Object value, SmofType type) {
		final BsonParser parser = parsers.get(type);
		return parser.toBson(value, null);
	}

	private void checkValidType(Object value, SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		final Class<?> type = value.getClass();
		if(!parser.isValidType(type, field)) {
			handleError(new InvalidTypeException(type, field.getType()));
		}
	}

	private void checkValidBson(BsonValue value, SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		if(!parser.isValidBson(value)) {
			handleError(new InvalidBsonTypeException(value.getBsonType(), field.getName()));
		}
	}

	boolean isValidType(SmofType smofType, Class<?> type) {
		final BsonParser parser = parsers.get(smofType);
		return parser.isValidType(type, null);
	}
}
