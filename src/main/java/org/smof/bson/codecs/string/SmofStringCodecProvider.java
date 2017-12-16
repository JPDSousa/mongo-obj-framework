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
package org.smof.bson.codecs.string;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

@SuppressWarnings("javadoc")
public class SmofStringCodecProvider implements CodecProvider {
	
	private final Codec<String> stringCodec;
	private final Codec<Integer> integerCodec;
	
	public SmofStringCodecProvider() {
		stringCodec = new StringCodec();
		integerCodec = new IntegerCodec();
	}

	@SuppressWarnings({ "unchecked", "cast", "rawtypes" })
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(Integer.class.equals(clazz)) {
			return (Codec<T>) integerCodec;
		}
		if(String.class.equals(clazz)) {
			return (Codec<T>) stringCodec;
		}
		if(Enum.class.isAssignableFrom(clazz)) {
			return (Codec<T>) new EnumCodec(clazz);
		}
		return null;
	}

}
