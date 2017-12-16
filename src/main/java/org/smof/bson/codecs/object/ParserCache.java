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
package org.smof.bson.codecs.object;

import java.util.Map;

import org.bson.BsonValue;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class ParserCache {
	
	public static ParserCache create() {
		return new ParserCache();
	}

	private final Map<Object, BsonValue> parserCache;
	
	private ParserCache() {
		parserCache = Maps.newHashMap();
	}
	
	public void put(Object object, BsonValue serializedValue) {
		parserCache.put(object, serializedValue);
	}
	
	public boolean containsObject(Object object) {
		return parserCache.containsKey(object);
	}
	
	public BsonValue getBsonValue(Object object) {
		return parserCache.get(object);
	}
	
	public void remove(Object key) {
		parserCache.remove(key);
	}	
	
}
