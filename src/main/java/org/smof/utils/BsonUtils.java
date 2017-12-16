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
package org.smof.utils;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonJavaScript;
import org.bson.BsonMaxKey;
import org.bson.BsonMinKey;
import org.bson.BsonNull;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonSymbol;
import org.bson.BsonType;
import org.bson.BsonUndefined;
import org.bson.BsonValue;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.BsonLazyObjectId;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class BsonUtils {
	
	/** This class not support new instances.
	/* 
	/**/
	private BsonUtils() {}
	
	public static <T> Codec<T> getCodecOrThrow(Class<T> clazz, CodecRegistry registry) {
		final Codec<T> codec = registry.get(clazz);
		if(codec == null) {
			throw new RuntimeException("Cannot find a valid codec for " + clazz.getName());
		}
		return codec;
	}
	
	public static BsonObjectId toBsonObjectId(Element value) {
		final ObjectId id = value.getId();
		return new BsonObjectId(id);
	}
	
	public static Stack<Pair<String, Element>> extrackPosInsertions(BsonDocument doc) {
		final BsonArray bsonPosInsert = doc.remove(SmofParser.ON_INSERT).asArray();
		final Stack<Pair<String, Element>> posInsertions = new Stack<>();
		bsonPosInsert.stream()
			.map(v -> (BsonLazyObjectId) v)
			.map(id -> Pair.of(id.getFieldName(), id.getElement()))
			.forEach(posInsertions::push);
		return posInsertions;
	}
	
	public static BsonDocument readDocument(BsonReader reader) {
		final BsonDocument document = new BsonDocument();
		reader.readStartDocument();
		while(reader.getCurrentBsonType() != BsonType.END_OF_DOCUMENT) {
			document.append(reader.readName(), readValue(reader));
		}
		reader.readEndDocument();
		return document;
	}
	
	public static BsonValue readValue(BsonReader reader) {
		switch(reader.getCurrentBsonType()) {
		case ARRAY:
			return readArray(reader);
		case BINARY:
			return reader.readBinaryData();
		case BOOLEAN:
			return new BsonBoolean(reader.readBoolean());
		case DATE_TIME:
			return new BsonDateTime(reader.readDateTime());
		case DB_POINTER:
			return reader.readDBPointer();
		case DOCUMENT:
			return readDocument(reader);
		case DOUBLE:
			return new BsonDouble(reader.readDouble());
		case END_OF_DOCUMENT:
			return null;
		case INT32:
			return new BsonInt32(reader.readInt32());
		case INT64:
			return new BsonInt64(reader.readInt64());
		case JAVASCRIPT:
			return new BsonJavaScript(reader.readJavaScript());
		case JAVASCRIPT_WITH_SCOPE:
			return null;
		case MAX_KEY:
			reader.readMaxKey();
			return new BsonMaxKey();
		case MIN_KEY:
			reader.readMinKey();
			return new BsonMinKey();
		case NULL:
			reader.readNull();
			return new BsonNull();
		case OBJECT_ID:
			return new BsonObjectId(reader.readObjectId());
		case REGULAR_EXPRESSION:
			return reader.readRegularExpression();
		case STRING:
			return new BsonString(reader.readString());
		case SYMBOL:
			return new BsonSymbol(reader.readSymbol());
		case TIMESTAMP:
			return reader.readTimestamp();
		case UNDEFINED:
			reader.readUndefined();
			return new BsonUndefined();
		case DECIMAL128:
			return new BsonDecimal128(reader.readDecimal128());
		default:
			return null;
		}
	}

	public static BsonArray readArray(BsonReader reader) {
		final BsonArray array = new BsonArray();
		reader.readStartArray();
		while(reader.getCurrentBsonType() != BsonType.END_OF_DOCUMENT) {
			array.add(readValue(reader));
		}
		reader.readEndArray();
		return array;
	}
	
	public static void setElementMetadata(BsonDocument document, Object obj) {
		if(obj instanceof Element) {
			((Element) obj).setId(document.getObjectId(Element.ID).getValue());
		}
	}

}
