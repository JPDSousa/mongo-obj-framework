package org.smof.bson.codecs.object;

import static org.smof.utils.BsonUtils.*;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.smof.annnotations.SmofObject;
import org.smof.bson.codecs.SmofCodec;
import org.smof.bson.codecs.SmofEncoderContext;
import org.smof.field.PrimaryField;
import org.smof.field.SecondaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

import com.google.common.collect.Maps;

class MapCodec implements SmofCodec<Map<String, Object>> {

	private final SmofParser topParser;
	
	public MapCodec(SmofParser topParser) {
		super();
		this.topParser = topParser;
	}

	@Override
	public void encode(BsonWriter writer, Map<String, Object> value, EncoderContext encoderContext) {
		encodeAsDocument(writer, value, null);
	}

	@Override
	public void encode(BsonWriter writer, Map<String, Object> value, SmofEncoderContext context) {
		final SmofField field = context.getField();
		if(field instanceof PrimaryField) {
			final PrimaryField primaryField = (PrimaryField) field;
			final SecondaryField secondaryField = primaryField.getSecondaryField();
			final SmofObject annotation = primaryField.getSmofAnnotationAs(SmofObject.class);
			encodeMap(writer, value, secondaryField, annotation);
		}
		else if(field instanceof SecondaryField) {
			final SecondaryField secondaryField = (SecondaryField) field;
			encodeMap(writer, value, secondaryField, secondaryField.getParentAnnotationAs(SmofObject.class));
		}
		else {
			throw new UnsupportedOperationException("Cannot handle field: " + field);
		}
	}
	
	private void encodeMap(BsonWriter writer, Map<String, Object> value, SecondaryField field, SmofObject annotation) {
		if(annotation.encodeMapAsArray()) {
			encodeAsArray(writer, value, field);
		}
		else {
			encodeAsDocument(writer, value, field);
		}
	}
	
	private void encodeAsArray(BsonWriter writer, Map<String, Object> value, SecondaryField field) {
		writer.writeStartArray();
		for(Map.Entry<String, Object> entry : value.entrySet()) {
			final BsonDocument document = new BsonDocument();
			final BsonValue entryValue = topParser.toBson(entry.getValue(), field);
			document.append("key", new BsonString(entry.getKey()));
			document.append("value", entryValue);
			writer.pipe(new BsonDocumentReader(document));
		}
		writer.writeEndArray();
	}

	private void encodeAsDocument(BsonWriter writer, Map<String, Object> value, SecondaryField field) {
		final BsonDocument document = new BsonDocument();
		for (Map.Entry<String, Object> entry : value.entrySet()) {
			final BsonValue entryValue = topParser.toBson(entry, field);
			document.append(entry.getKey(), entryValue);
		}
		writer.pipe(new BsonDocumentReader(document));
	}

	@SuppressWarnings({ "unchecked", "cast", "rawtypes" })
	@Override
	public Class<Map<String, Object>> getEncoderClass() {
		return (Class<Map<String, Object>>) ((Class) Map.class);
	}

	@Override
	public Map<String, Object> decode(BsonReader reader, DecoderContext decoderContext) {
		return decodeAsDocument(reader, null);
	}

	@Override
	public Map<String, Object> decode(BsonReader reader, SmofEncoderContext context) {
		final SmofField field = context.getField();
		if(field instanceof PrimaryField) {
			final PrimaryField primaryField = (PrimaryField) field;
			return decodeMap(reader, primaryField.getSecondaryField(), primaryField.getSmofAnnotationAs(SmofObject.class));
		}
		else if(field instanceof SecondaryField) {
			final SecondaryField secondaryField = (SecondaryField) field;
			return decodeMap(reader, secondaryField, secondaryField.getParentAnnotationAs(SmofObject.class));
		}
		throw new UnsupportedOperationException("Cannot handle field: " + field);
	}
	
	private Map<String, Object> decodeMap(BsonReader reader, SecondaryField field, SmofObject annotation) {
		if(annotation.encodeMapAsArray()) {
			return decodeAsArray(reader, field);
		}
		return decodeAsDocument(reader, field);
	}
	
	private Map<String, Object> decodeAsDocument(BsonReader reader, SmofField field) {
		final Map<String, Object> map = Maps.newHashMap();
		reader.readStartDocument();
		while(reader.getCurrentBsonType() != BsonType.END_OF_DOCUMENT) {
			final String key = reader.readName();
			final Object value = topParser.fromBson(readValue(reader), field);
			map.put(key, value);
		}
		reader.readEndDocument();
		return map;
	}
	
	private Map<String, Object> decodeAsArray(BsonReader reader, SmofField field) {
		final Map<String, Object> map = Maps.newHashMap();
		reader.readStartArray();
		while(reader.getCurrentBsonType() != BsonType.END_OF_DOCUMENT) {
			final String key;
			final Object value;
			reader.readStartDocument();
			reader.readName();
			key = reader.readString();
			reader.readName();
			value = topParser.fromBson(readValue(reader), field);
			reader.readEndDocument();
			map.put(key, value);
		}
		reader.readEndArray();
		return map;
	}

}
