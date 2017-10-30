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
package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.collection.SmofDispatcher;

import static org.smof.parsers.SmofType.*;

@SuppressWarnings("javadoc")
public class SmofParserPool {
	
	public static SmofParserPool create(SmofParser parserContext, SmofDispatcher dispatcher) {
		return new SmofParserPool(parserContext, dispatcher);
	}
	
	private final Map<SmofType, BsonParser> parsers;

	private SmofParserPool(SmofParser parserContext, SmofDispatcher dispatcher) {
		parsers = new LinkedHashMap<>();
		createParsers(parserContext, dispatcher);
	}
	
	public BsonParser get(SmofType type) {
		return parsers.get(type);
	}
	
	private void createParsers(SmofParser parserContext, SmofDispatcher dispatcher) {
		parsers.put(ARRAY, new ArrayParser(parserContext, dispatcher));
		parsers.put(DATETIME, new DateTimeParser(parserContext, dispatcher));
		parsers.put(NUMBER, new NumberParser(parserContext, dispatcher));
		parsers.put(OBJECT, new ObjectParser(parserContext, dispatcher));
		parsers.put(OBJECT_ID, new ObjectIdParser(parserContext, dispatcher));
		parsers.put(STRING, new StringParser(parserContext, dispatcher));
		parsers.put(BYTE, new ByteParser(dispatcher, parserContext));
		parsers.put(BOOLEAN, new BooleanParser(parserContext, dispatcher));
	}
}
