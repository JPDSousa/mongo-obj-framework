package org.smof.parsers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bson.BsonString;
import org.bson.BsonValue;
import org.smof.collection.SmofDispatcher;
import org.smof.field.SmofField;

class StringParser extends AbstractBsonParser {
	
	private static final Class<?>[] VALID_TYPES = {
			String.class, Enum.class, Integer.class/*int.class, long.class, 
			short.class, double.class, float.class,	char.class, 
			byte.class, byte[].class, Integer.class, Long.class,
			Short.class, Double.class, Float.class, Character.class,
			Byte.class, Byte[].class*/};

	StringParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, VALID_TYPES);
	}

	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		final String bson;
		if(isEnum(type)) {
			bson = fromEnum(value);
		}
		else if(isString(type)) {
			bson = (String) value;
		}
		else if(isInteger(type)) {
			bson = ((Integer) value).toString();
		}
		else {
			//TODO add other type
			bson = null;
		}
		return new BsonString(bson);
	}

	private boolean isInteger(Class<?> type) {
		return Integer.class.equals(type);
	}

	private String fromEnum(Object value) {
		return ((Enum<?>) value).name();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue rawValue, Class<T> type, SmofField fieldOpts) {
		final String value = rawValue.asString().getValue();
		if (isString(type)) {
			return (T) value;
		}
		else if(isEnum(type)) {
			return toEnum(value, type);
		}
		else if(isInteger(type)) {
			return (T) new Integer(value);
		}
		else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T toEnum(String value, Class<T> type) {
		try {
			final Method valueOf = getValueOf(type);
			final T res;
			valueOf.setAccessible(true);
			res = (T) valueOf.invoke(null, value);
			valueOf.setAccessible(false);
			return res;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			//all enums have valueOf!!
			handleError(e);
			return null;
		}
	}

	private Method getValueOf(Class<?> enumType) throws NoSuchMethodException, SecurityException {
		return enumType.getMethod("valueOf", String.class);
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) || value.isString();
	}

}
