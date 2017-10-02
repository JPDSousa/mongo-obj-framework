/*******************************************************************************
 * Copyright (C) 2017 Joao
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.parsers;

import java.math.BigDecimal;

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
		super(dispatcher, parser, null, VALID_TYPES);
	}

	@Override
	public BsonValue serializeToBson(Object value, SmofField fieldOpts) {
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
			final BigDecimal value = new BigDecimal(numVal.asDouble().getValue());
			retValue = value.floatValue();
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

	private boolean isDouble(Class<?> type) {
		return type.equals(Double.class) || type.equals(double.class);
	}

	private boolean isLong(Class<?> type) {
		return type.equals(Long.class) || type.equals(long.class);
	}

}
