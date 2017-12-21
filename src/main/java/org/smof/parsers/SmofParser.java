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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonElement;
import org.bson.BsonNull;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.object.ParserCache;
import org.smof.collection.SmofDispatcher;
import org.smof.element.Element;
import org.smof.exception.InvalidBsonTypeException;
import org.smof.exception.SmofException;
import org.smof.field.MasterField;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.index.InternalIndex;
import org.smof.parsers.metadata.SmofTypeContext;
import org.smof.parsers.metadata.TypeStructure;

import com.google.common.collect.Maps;
import com.mongodb.MongoClient;

@SuppressWarnings("javadoc")
public class SmofParser {
	
	public static final String ON_INSERT = "onInsert";

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final SmofDispatcher dispatcher;
	private final SmofTypeContext context;
	private final SmofParserPool parsers;
	private final CodecRegistry registry;
	
	private final ParserCache parserCache;
	private final Map<Object, List<Callable<BsonElement>>> posInsertionHooks;

	public SmofParser(SmofDispatcher dispatcher) {
		this(dispatcher, MongoClient.getDefaultCodecRegistry());
	}
	
	public SmofParser(SmofDispatcher dispatcher, CodecRegistry registry) {
		this.dispatcher = dispatcher;
		this.context = SmofTypeContext.create();
		parsers = SmofParserPool.create(this, dispatcher);
		parserCache = ParserCache.create();
		posInsertionHooks = Maps.newHashMap();
		this.registry = CodecRegistries.fromRegistries(registry, parsers.getRegistry());
	}
	
	public ParserCache getCache() {
		return parserCache;
	}
	
	public Map<Object, List<Callable<BsonElement>>> getPosInsertionHooks() {
		return posInsertionHooks;
	}

	public CodecRegistry getRegistry() {
		return registry;
	}

	public SmofTypeContext getContext() {
		return context;
	}
	
	public <T> TypeStructure<T> getTypeStructure(Class<T> type) {
		return getContext().getTypeStructure(type, parsers);
	}

	public void registerType(Class<?> type) {
		context.put(type, parsers);
	}

	public void registerType(Class<?> type, Serializable factory){
		context.putWithFactory(type, factory, parsers);
	}

	public <T extends Element> T fromBson(BsonDocument document, Class<T> type) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		final MasterField field = new MasterField(type);
		return parser.fromBson(document, type, field);
	}

	public Object fromBson(BsonValue value, SmofField field) {
		if(value.isNull()) {
			return null;
		}
		checkValidBson(value, field);
		final BsonParser parser = parsers.get(field.getType());
		final Class<?> type = field.getFieldClass();

		return fromBson(parser, value, type, field);
	}

	private Object fromBson(BsonParser parser, BsonValue value, Class<?> type, SmofField field) {
		if(value.isNull()) {
			return null;
		}
		return parser.fromBson(value, type, field);
	}

	public BsonDocument toBson(Element value) {
		final BsonParser parser = parsers.get(SmofType.OBJECT);
		final MasterField field = new MasterField(value.getClass());
		
		return (BsonDocument) parser.toBson(value, field);
	}
	
	@SuppressWarnings("unchecked")
	public <T> BsonValue toBson(Object value, Class<T> clazz) {
		if(value == null) {
			return new BsonNull();
		}
		if(value instanceof BsonValue) {
			return (BsonValue) value;
		}
		final Codec<T> codec = registry.get(clazz);
		final String key = "value";
		final BsonDocument document = new BsonDocument();
		final BsonWriter writer = new BsonDocumentWriter(document);
		writer.writeStartDocument();
		writer.writeName(key);
		codec.encode(writer, (T) value, EncoderContext.builder().build());
		writer.writeEndDocument();
		return document.get(key);
	}

	public BsonValue toBson(Object value, SmofField field) {
		if(value == null) {
			return new BsonNull();
		}
		if(value instanceof BsonValue) {
			return (BsonValue) value;
		}
		if(field == null) {
			return toBson(value, value.getClass());
		}
		final SmofType type = field.getType();
		final BsonParser parser = parsers.get(type);
		return parser.toBson(value, field);
	}

	private void checkValidBson(BsonValue value, SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		if(!parser.isValidBson(value)) {
			handleError(new InvalidBsonTypeException(value.getBsonType(), field.getName()));
		}
	}

	protected boolean isValidType(SmofField field) {
		final BsonParser parser = parsers.get(field.getType());
		return parser.isValidType(field.getFieldClass());
	}

	public SmofParserPool getParsers() {
		return parsers;
	}

	public <T extends Element> Set<InternalIndex> getIndexes(Class<T> elClass) {
		return context.getIndexes(elClass);
	}

	public PrimaryField getField(Class<?> type, String fieldName) {
		return getTypeStructure(type).getAllFields().get(fieldName);
	}

	public SmofDispatcher getDispatcher() {
		return dispatcher;
	}
}
