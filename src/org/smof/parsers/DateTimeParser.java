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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.bson.BsonValue;
import org.smof.bson.codecs.SmofCodecProvider;
import org.smof.bson.codecs.date.SmofDateCodecProvider;
import org.smof.collection.SmofDispatcher;

class DateTimeParser extends AbstractBsonParser {
	
	private static final Class<?>[] VALID_TYPES = {Instant.class, LocalDate.class, LocalDateTime.class, 
			ZonedDateTime.class};
	static final SmofCodecProvider PROVIDER = new SmofDateCodecProvider();
	
	DateTimeParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, PROVIDER, VALID_TYPES);
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) 
				|| value.isDateTime()
				|| value.isDouble()
				|| value.isString()
				|| value.isInt32()
				|| value.isInt64();
	}

}
