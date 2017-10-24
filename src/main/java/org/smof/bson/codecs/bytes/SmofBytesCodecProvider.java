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
import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;
import org.smof.field.SmofField;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class SmofBytesCodecProvider implements SmofCodecProvider {

	private final Map<Class<?>, Codec<?>> codecs;
	
	public SmofBytesCodecProvider() {
		codecs = Maps.newLinkedHashMap();
		put(new PrimitiveByteArrayCodec());
		put(new GenericByteArrayCodec());
	}
	
	private void put(Codec<?> codec) {
		codecs.put(codec.getEncoderClass(), codec);
	}
	
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		return get(clazz, registry, null);
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return codecs.containsKey(clazz) || Collection.class.isAssignableFrom(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> type, CodecRegistry registry, SmofField field) {
		if(Collection.class.isAssignableFrom(type)) {
			return (Codec<T>) new ByteCollectionCodec((Class<Collection<Byte>>) type);
		}
		return (Codec<T>) codecs.get(type);
	}

}
