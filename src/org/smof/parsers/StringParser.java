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
	protected BsonValue serializeToBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		final String bson;
		if(isEnum(type)) {
			bson = fromEnum(value);
		}
		else if(isString(type)) {
			bson = (String) value;
		}
		else if(isInteger(type)) {
			bson = value.toString();
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
