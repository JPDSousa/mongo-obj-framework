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

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.smof.bson.codecs.SmofCodecProvider;
import org.smof.collection.SmofDispatcher;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.field.MasterField;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.gridfs.SmofGridRef;

abstract class AbstractBsonParser implements BsonParser {
	
	protected static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	protected final SmofCodecProvider provider;
	protected final SmofParser bsonParser;
	protected final SmofDispatcher dispatcher;
	
	protected AbstractBsonParser(SmofDispatcher dispatcher, SmofParser bsonParser, SmofCodecProvider provider) {
		this.provider = provider;
		this.bsonParser = bsonParser;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final SerializationContext serContext = bsonParser.getSerializationContext();
		if(serContext.contains(value, fieldOpts.getType())) {
			return serContext.get(value, fieldOpts.getType());
		}
		final BsonValue serValue = serializeToBson(value, fieldOpts);
		serContext.put(value, fieldOpts.getType(), serValue);
		return serValue;
	}
	
	@SuppressWarnings("unused")
	protected BsonValue serializeToBson(Object value, SmofField fieldOpts) {
		final Class<? extends Object> clazz = value.getClass();
		final CodecRegistry registry = bsonParser.getRegistry();
		return serializeWithCodec(provider.get(clazz, registry), value);
	}
	
	@SuppressWarnings("unchecked")
	protected final <T> BsonValue serializeWithCodec(Codec<T> codec, Object value) {
		final BsonDocument document = new BsonDocument();
		final String name = "result";
		final BsonDocumentWriter writer = new BsonDocumentWriter(document);
		writer.writeStartDocument();
		writer.writeName(name);
		codec.encode(writer, (T) value, EncoderContext.builder().build());
		writer.writeEndDocument();
		return document.get(name);
	}
	
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		final Codec<T> codec = provider.get(type, bsonParser.getRegistry());
		return deserializeWithCodec(codec, value);
	}
	
	protected final <T> T deserializeWithCodec(Codec<T> codec, BsonValue value) {
		final BsonDocument document = new BsonDocument("result", value);
		final BsonReader reader = new BsonDocumentReader(document);
		reader.readStartDocument();
		reader.readName();
		return codec.decode(reader, DecoderContext.builder().build());
	}

	protected <T> TypeStructure<T> getTypeStructure(Class<T> type) {
		return bsonParser.getContext().getTypeStructure(type, bsonParser.getParsers());
	}
	
	protected <T> TypeParser<T> getTypeParser(Class<T> type) {
		return getTypeStructure(type).getParser(type);
	}
	
	protected <T> TypeBuilder<T> getTypeBuilder(Class<T> type) {
		return getTypeStructure(type).getBuilder();
	}

	@Override
	public boolean isValidType(SmofField fieldOpts) {
		return isValidType(fieldOpts.getFieldClass());
	}

	@Override
	public boolean isValidType(Class<?> type) {
		return provider.contains(type);
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return value.isNull();
	}

	protected boolean isEnum(final Class<?> type) {
		return type.isEnum();
	}

	protected boolean isMap(final Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}

	protected boolean isString(Class<?> type) {
		return type.equals(String.class);
	}

	protected boolean isElement(Class<?> type) {
		return Element.class.isAssignableFrom(type);
	}
	
	protected boolean isSmofGridRef(Class<?> type) {
		return SmofGridRef.class.isAssignableFrom(type);
	}
	
	protected boolean isPrimaryField(SmofField fieldOpts) {
		return fieldOpts instanceof PrimaryField;
	}
	
	protected boolean isParameterField(SmofField field) {
		return field instanceof ParameterField;
	}

	protected boolean isMaster(SmofField fieldOpts) {
		return fieldOpts instanceof MasterField;
	}
	
	protected boolean isPrimitive(Class<?> type) {
		return type.isPrimitive();
	}
	
	protected boolean isArray(Class<?> type) {
		return type.isArray();
	}

}
