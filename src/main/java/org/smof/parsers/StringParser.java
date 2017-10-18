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

import org.bson.BsonValue;
import org.smof.bson.codecs.SmofCodecProvider;
import org.smof.bson.codecs.string.SmofStringCodecProvider;
import org.smof.collection.SmofDispatcher;

class StringParser extends AbstractBsonParser {

	private static final long serialVersionUID = 1L;
	
	private static final Class<?>[] VALID_TYPES = {
			String.class, Enum.class, Integer.class};
	static final SmofCodecProvider PROVIDER = new SmofStringCodecProvider();
	
	StringParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, PROVIDER, VALID_TYPES);
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) 
				|| value.isString()
				|| value.isBinary()
				|| value.isBoolean()
				|| value.isDateTime()
				|| value.isDouble()
				|| value.isInt32()
				|| value.isInt64()
				|| value.isJavaScript()
				|| value.isJavaScriptWithScope()
				|| value.isObjectId()
				|| value.isRegularExpression()
				|| value.isSymbol();
	}

	@Override
	public boolean isValidType(Class<?> type) {
		return isEnum(type) || super.isValidType(type);
	}
	
	

}
