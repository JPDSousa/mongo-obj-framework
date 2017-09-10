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

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonValue;

import com.google.common.collect.Maps;

class SerializationContext {
	
	public static SerializationContext create() {
		return new SerializationContext();
	}

	private final Map<Pair<Object, SmofType>, BsonValue> serializationStack;
	
	private SerializationContext() {
		serializationStack = Maps.newLinkedHashMap();
	}
	
	void put(Object object, SmofType type, BsonValue serializedValue) {
		serializationStack.put(Pair.of(object, type), serializedValue);
	}
	
	boolean contains(Object object, SmofType type) {
		return serializationStack.containsKey(Pair.of(object, type));
	}
	
	BsonValue get(Object object, SmofType type) {
		return serializationStack.get(Pair.of(object, type));
	}
	
	void clear() {
		serializationStack.clear();
	}	
	
}
