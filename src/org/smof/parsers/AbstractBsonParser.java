package org.smof.parsers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.BsonValue;
import org.smof.collection.SmofDispatcher;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.field.MasterField;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;

abstract class AbstractBsonParser implements BsonParser {
	
	protected static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	protected final List<Class<?>> validTypes;
	protected final SmofParser bsonParser;
	protected final SmofDispatcher dispatcher;
	
	protected AbstractBsonParser(SmofDispatcher dispatcher, SmofParser bsonParser, Class<?>... validTypes) {
		this.validTypes = Arrays.asList(validTypes);
		this.bsonParser = bsonParser;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts, SerializationContext serContext) {
		if(serContext.contains(value, fieldOpts.getType())) {
			return serContext.get(value, fieldOpts.getType());
		}
		final BsonValue serValue = toBson(value, fieldOpts);
		serContext.put(value, fieldOpts.getType(), serValue);
		return serValue;
	}
	
	protected abstract BsonValue toBson(Object value, SmofField fieldOpts);

	@Override
	public abstract <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts);

	protected <T> TypeStructure<T> getTypeStructure(Class<T> type) {
		return bsonParser.getContext().getTypeStructure(type, bsonParser.getParsers());
	}
	
	protected <T> TypeParser<T> getTypeParser(Class<T> type) {
		return getTypeStructure(type).getParser(type);
	}
	
	protected <T> TypeBuilder<T> getTypeBuilder(Class<T> type) {
		return getTypeStructure(type).getBuilder();
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
	
	protected boolean isParameterField(SmofField field) {
		return field instanceof ParameterField;
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
