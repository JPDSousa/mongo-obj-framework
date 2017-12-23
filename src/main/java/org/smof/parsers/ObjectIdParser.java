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

import static com.google.common.base.Preconditions.*;

import org.bson.BsonValue;
import org.bson.codecs.Codec;
import org.bson.codecs.ObjectIdCodec;
import org.bson.types.ObjectId;
import org.smof.collection.SmofDispatcher;
import org.smof.field.SmofField;

class ObjectIdParser extends AbstractBsonParser {
	
	private static final Class<?>[] VALID_TYPES = {ObjectId.class};
	
	private final Codec<ObjectId> codec;
	
	ObjectIdParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, null, VALID_TYPES);
		this.codec = new ObjectIdCodec();
	}
	
	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		checkArgument(value != null, "The value cannot be null");
		return serializeWithCodec(codec, value, fieldOpts);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		return (T) value.asObjectId().getValue();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) || value.isObjectId();
	}

}
