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

import java.util.Collection;
import java.util.Iterator;

import org.bson.BsonBinary;
import org.bson.BsonInvalidOperationException;
import org.smof.utils.CollectionUtils;

class ByteCollectionCodec extends AbstractBytesCodec<Collection<Byte>> {

	private final Class<Collection<Byte>> encoderClass;
	
	public ByteCollectionCodec(Class<Collection<Byte>> encoderClass) {
		super();
		this.encoderClass = encoderClass;
	}

	@Override
	public Class<Collection<Byte>> getEncoderClass() {
		return encoderClass;
	}

	@Override
	protected BsonBinary encode(Collection<Byte> value) {
		final byte[] data = new byte[value.size()];
		final Iterator<Byte> iterator = value.iterator();
		int i = 0;
		while(iterator.hasNext()) {
			data[i++] = iterator.next();
		}
		return new BsonBinary(data);
	}

	@Override
	protected Collection<Byte> decode(BsonBinary bsonValue) {
		final Collection<Byte> value = getInstance();
		for(byte bsonByte : bsonValue.getData()) {
			value.add(bsonByte);
		}
		return value;
	}

	private Collection<Byte> getInstance() {
		try {
			return CollectionUtils.create(encoderClass);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
				| SecurityException e) {
			throw new BsonInvalidOperationException(e.getMessage());
		}
	}

}
