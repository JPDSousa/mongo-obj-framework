package org.smof.parsers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import org.bson.BsonValue;
import org.smof.annnotations.SmofField;
import org.smof.exception.SmofException;

abstract class AbstractBsonParser implements BsonParser {
	
	protected final List<Class<?>> validTypes;
	protected final SmofParser bsonParser;
	
	protected AbstractBsonParser(SmofParser bsonParser, Class<?>... validTypes) {
		this.validTypes = Arrays.asList(validTypes);
		this.bsonParser = bsonParser;
	}
	
	protected void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	@Override
	public abstract BsonValue toBson(Object value, SmofField fieldOpts);

	@Override
	public abstract <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts);

	@Override
	public boolean isValidType(SmofField fieldOpts) {
		return isValidType(fieldOpts.getFieldClass());
	}

	@Override
	public boolean isValidType(Class<?> type) {
		return validTypes.stream().anyMatch(t -> t.isAssignableFrom(type));
	}

	@Override
	public abstract boolean isValidBson(BsonValue value);

	protected boolean isArray(final Class<?> type) {
		return type.isArray() && !type.getComponentType().isPrimitive();
	}

	protected boolean isEnum(final Class<?> type) {
		return type.isEnum();
	}

	protected boolean isMap(final Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}

	protected <T> AnnotationParser<T> getAnnotationParser(Class<T> type) {
		return bsonParser.getContext().getMetadata(type);
	}

	protected boolean isString(Class<?> type) {
		return type.equals(String.class);
	}

	protected boolean isElement(Class<?> type) {
		return Element.class.isAssignableFrom(type);
	}

}
