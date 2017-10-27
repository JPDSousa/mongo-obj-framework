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
package org.smof.bson.codecs.date;

import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class SmofDateCodecProvider implements SmofCodecProvider {

	private final Map<Class<?>, Codec<?>> codecs;
	
	public SmofDateCodecProvider() {
		codecs = Maps.newHashMapWithExpectedSize(4);
		put(new JavaInstantCodec());
		put(new JavaLocalDateCodec());
		put(new JavaLocalDateTimeCodec());
		put(new JavaZonedDateTimeCodec());
	}
	
	private <T> void put(Codec<T> codec) {
		codecs.put(codec.getEncoderClass(), codec);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		return (Codec<T>) codecs.get(clazz);
	}

	@Override
	public boolean contains(Class<?> clazz) {
		return codecs.containsKey(clazz);
	}

}
