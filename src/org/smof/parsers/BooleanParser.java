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

import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.smof.collection.SmofDispatcher;
import org.smof.field.SmofField;

import java.util.Objects;

@SuppressWarnings("javadoc")
public class BooleanParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {boolean.class, Boolean.class};

	protected BooleanParser(SmofParser bsonParser, SmofDispatcher dispatcher) {
		super(dispatcher, bsonParser, VALID_TYPES);
	}

	@Override
	protected BsonValue serializeToBson(Object value, SmofField fieldOpts) {
		if (Objects.isNull(value)) {
			throw new RuntimeException("You must specify a value in order to be serialized");
		}
		if(isBoolean(value.getClass())) {
			return fromBoolean((Boolean) value);
		}
		return null;
	}

	private BsonValue fromBoolean(Boolean value) {
		return new BsonBoolean(value);
	}

	private boolean isBoolean(Class<?> type) {
		return type.equals(boolean.class) || type.equals(Boolean.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		if (Objects.isNull(value)) {
			throw new RuntimeException("A value must be specified.");
		}
		return (T) Boolean.valueOf(value.asBoolean().getValue());
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) || value.isBoolean();
	}

}