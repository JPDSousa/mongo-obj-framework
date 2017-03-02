package org.smof.parsers;

import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.smof.collection.SmofDispatcher;
import org.smof.element.Element;
import org.smof.exception.InvalidBsonTypeException;
import org.smof.exception.SmofException;
import org.smof.field.MasterField;
import org.smof.field.SmofField;

@SuppressWarnings("javadoc")
public class SmofParser {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final SmofTypeContext context;
	private final SmofParserPool parsers;
	private final LazyLoader lazyLoader;

	public SmofParser(SmofDispatcher dispatcher) {
		this.context = new SmofTypeContext();
		parsers = SmofParserPool.create(this, dispatcher);
		lazyLoader = LazyLoader.create(dispatcher);
	}

	SmofTypeContext getContext() {
		return context;
	}
	
	<T extends Element> T createLazyInstance(Class<T> type, ObjectId id) {
		return lazyLoader.createLazyInstance(type, id);
	}

	public <T> TypeParser<T> getTypeParser(Class<T> type) {
		return context.getTypeParser(type, parsers);
	}

	public void registerType(Class<?> type) {
		context.put(type, parsers);
	}

	public void registerType(Class<?> type, Object factory){
		context.putWithFactory(type, factory, parsers);
	}

	public <T extends Element> T fromBson(BsonDocument document, Class<T> type) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		final MasterField field = new MasterField(type);
		return parser.fromBson(document, type, field);
	}

	Object fromBson(BsonValue value, SmofField field) {
		checkValidBson(value, field);
		final BsonParser parser = parsers.get(field.getType());
		final Class<?> type = field.getFieldClass();

		return fromBson(parser, value, type, field);
	}

	private Object fromBson(BsonParser parser, BsonValue value, Class<?> type, SmofField field) {
		if(value.isNull()) {
			return null;
		}
		return parser.fromBson(value, type, field);
	}

	public BsonDocument toBson(Element value) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		final MasterField field = new MasterField(value.getClass());
		return (BsonDocument) parser.toBson(value, field);
	}

	BsonValue toBson(Object value, SmofField field) {
		if(value == null) {
			return new BsonNull();
		}
		final SmofType type = field.getType();
		final BsonParser parser = parsers.get(type);
		return parser.toBson(value, field);
	}

	private void checkValidBson(BsonValue value, SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		if(!parser.isValidBson(value)) {
			handleError(new InvalidBsonTypeException(value.getBsonType(), field.getName()));
		}
	}

	boolean isValidType(SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		return parser.isValidType(field.getFieldClass());
	}

	SmofParserPool getParsers() {
		return parsers;
	}
}
