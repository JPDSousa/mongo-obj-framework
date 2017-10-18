/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
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
package org.smof.bson.codecs.bool;

import static java.lang.String.format;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

abstract class AbstractBooleanCodec<T> implements Codec<T> {

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		writer.writeBoolean(encode(value));
	}

	protected abstract boolean encode(T value);

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonType type = reader.getCurrentBsonType();
		switch(type) {
		case INT32:
			return decode(reader.readInt32() != 0);
		case NULL:
			return null;
		case STRING:
			return decode(Boolean.parseBoolean(reader.readString()));
		case BOOLEAN:
			return decode(reader.readBoolean());
			//$CASES-OMITTED$
		default:
			final String errMsg = format("Invalid date type, found: %s", type);
			throw new BsonInvalidOperationException(errMsg); 
		}
	}
	
	protected abstract T decode(boolean value);

}
