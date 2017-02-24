package org.smof.parsers;

import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNumber;
import org.bson.BsonValue;
import org.smof.collection.SmofDispatcher;
import org.smof.field.SmofField;

class NumberParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {int.class,
			long.class, short.class, double.class, float.class,
			Integer.class, Long.class, Short.class, Double.class,
			Float.class};

	NumberParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, VALID_TYPES);
	}

	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		if(isInteger(type)) {
			return writeInteger((Integer) value);
		} 
		else if(isShort(type)) {
			return writeShort((Short) value);
		}
		else if(isFloat(type)) {
			return writeFloat((Float) value);
		}
		else if(isDouble(type)) {
			return writeDouble((Double) value);
		}
		else if(isLong(type)) {
			return writeLong((Long) value);
		}
		return null;
	}

	private BsonInt32 writeInteger(Integer value) {
		return new BsonInt32(value);
	}

	private BsonInt32 writeShort(Short value) {
		return new BsonInt32(value);
	}

	private BsonDouble writeFloat(Float value) {
		return new BsonDouble(value);
	}

	private BsonDouble writeDouble(Double value) {
		return new BsonDouble(value);
	}

	private BsonInt64 writeLong(Long value) {
		return new BsonInt64(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue rawValue, Class<T> type, SmofField fieldOpts) {
		final BsonNumber numVal = rawValue.asNumber();
		final Object retValue;
		if(isInteger(type)) {
			retValue = readInteger(numVal);
		}
		else if(isShort(type)) {
			final int value = readInteger(numVal);
			validateShort(value);
			retValue = writeShort(value);
		}
		else if(isFloat(type)) {
			final double value = numVal.asDouble().getValue();
			validateFloat(value);
			retValue = readFloat(value);
		}
		else if(isDouble(type)) {
			retValue = readDouble(numVal);
		}
		else if(isLong(type)) {
			retValue = readLong(numVal);
		}
		else {
			retValue = null;
		}
		return (T) retValue;
	}

	private int readInteger(final BsonNumber numVal) {
		return numVal.asInt32().getValue();
	}

	private short writeShort(final int value) {
		return (short) value;
	}

	private Float readFloat(double value) {
		return (float) value;
	}

	private Double readDouble(BsonNumber value) {
		return value.asDouble().getValue();
	}

	private Long readLong(BsonNumber value) {
		return value.asInt64().getValue();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) || value.isNumber();
	}

	private boolean isInteger(Class<?> type) {
		return type.equals(Integer.class) || type.equals(int.class);
	}

	private boolean isShort(Class<?> type) {
		return type.equals(Short.class) || type.equals(short.class);
	}

	private void validateShort(int value) {
		if(value > Short.MAX_VALUE || value < Short.MIN_VALUE) {
			handleError(new IllegalArgumentException(value + " is not valid value for a short."));
		}
	}

	private boolean isFloat(Class<?> type) {
		return type.equals(Float.class) || type.equals(float.class);
	}

	private void validateFloat(double value) {
		if(value > Float.MAX_VALUE || value < Float.MIN_VALUE) {
			handleError(new IllegalArgumentException(value + " is not valid value for a float."));
		}
	}

	private boolean isDouble(Class<?> type) {
		return type.equals(Double.class) || type.equals(double.class);
	}

	private boolean isLong(Class<?> type) {
		return type.equals(Long.class) || type.equals(long.class);
	}

}
