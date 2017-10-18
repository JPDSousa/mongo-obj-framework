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
package org.smof.bson.codecs.bytes;

import static java.lang.String.format;

import org.bson.BsonBinary;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

abstract class AbstractBytesCodec<T> implements Codec<T> {

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		writer.writeBinaryData(encode(value));
	}
	
	protected abstract BsonBinary encode(T value);

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		final BsonType type = reader.getCurrentBsonType();
		if(type == BsonType.BINARY) {
			return decode(reader.readBinaryData());
		}
		final String errMsg = format("Invalid date type, found: %s", type);
		throw new BsonInvalidOperationException(errMsg); 
	}
	
	protected abstract T decode(BsonBinary value);

}
