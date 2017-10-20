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

import java.util.Arrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonNull;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
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

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;

@SuppressWarnings({ "javadoc", "serial" })
public class SmofParser implements Serializable {

	public static final String ON_INSERT = "onInsert";
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final SmofTypeContext context;
	private final SmofParserPool parsers;
	private final LazyLoader lazyLoader;
	private final SerializationContext serContext;
	private transient CodecRegistry registry;

	public SmofParser(SmofDispatcher dispatcher) {
		this(dispatcher, MongoClient.getDefaultCodecRegistry());
	}

	public SmofParser(SmofDispatcher dispatcher, CodecRegistry registry) {
		this.context = SmofTypeContext.create();
		serContext = SerializationContext.create();
		parsers = SmofParserPool.create(this, dispatcher);
		this.registry = loadRegistries(registry);
		lazyLoader = LazyLoader.create(dispatcher);
	}
	
	private CodecRegistry loadRegistries(CodecRegistry base) {
		final List<CodecProvider> providers = Lists.newArrayList();
		parsers.forEach(parser -> providers.add(parser.getProvider()));
		return CodecRegistries.fromRegistries(base, CodecRegistries.fromProviders(providers));
	}

	public CodecRegistry getRegistry() {
		return registry;
	}

	protected SmofTypeContext getContext() {
		return context;
	}

	protected SerializationContext getSerializationContext() {
		return serContext;
	}

	public <T> TypeStructure<T> getTypeStructure(Class<T> type) {
		return getContext().getTypeStructure(type, parsers);
	}

	protected <T extends Element> T createLazyInstance(Class<T> type, ObjectId id) {
		return lazyLoader.createLazyInstance(type, id);
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

	protected Object fromBson(BsonValue value, SmofField field) {
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

	protected SmofParserPool getParsers() {
		return parsers;
	}

	public <T extends Element> Set<InternalIndex> getIndexes(Class<T> elClass) {
		return context.getIndexes(elClass);
	}

	public void reset() {
		serContext.clear();
	}

	public PrimaryField getField(Class<?> type, String fieldName) {
		return getTypeStructure(type).getAllFields().get(fieldName);
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		registry = loadRegistries(MongoClient.getDefaultCodecRegistry());
	}
}
