package org.smof.parsers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.BsonValue;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.field.MasterField;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;

abstract class AbstractBsonParser implements BsonParser {
	
	protected static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	protected final List<Class<?>> validTypes;
	protected final SmofParser bsonParser;
	
	protected AbstractBsonParser(SmofParser bsonParser, Class<?>... validTypes) {
		this.validTypes = Arrays.asList(validTypes);
		this.bsonParser = bsonParser;
	}
	
	@Override
	public abstract BsonValue toBson(Object value, SmofField fieldOpts);

	@Override
	public abstract <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts);

	protected <T> AnnotationParser<T> getAnnotationParser(Class<T> type) {
		return bsonParser.getContext().getMetadata(type);
	}

	@Override
	public boolean isValidType(SmofField fieldOpts) {
		return isValidType(fieldOpts.getFieldClass());
	}

	@Override
	public boolean isValidType(Class<?> type) {
		return validTypes.stream().anyMatch(t -> t.isAssignableFrom(type));
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return value.isNull();
	}

	protected boolean isEnum(final Class<?> type) {
		return type.isEnum();
	}

	protected boolean isMap(final Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}

	protected boolean isString(Class<?> type) {
		return type.equals(String.class);
	}

	protected boolean isElement(Class<?> type) {
		return Element.class.isAssignableFrom(type);
	}
	
	protected boolean isPrimaryField(SmofField fieldOpts) {
		return fieldOpts instanceof PrimaryField;
	}

	protected boolean isMaster(SmofField fieldOpts) {
		return fieldOpts instanceof MasterField;
	}
	
	protected boolean isPrimitive(Class<?> type) {
		return type.isPrimitive();
	}
	
	protected boolean isArray(Class<?> type) {
		return type.isArray();
	}

}
