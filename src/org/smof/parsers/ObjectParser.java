package org.smof.parsers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.smof.annnotations.SmofField;
import org.smof.annnotations.SmofObject;
import org.smof.element.Element;
import org.smof.exception.MissingRequiredFieldException;

class ObjectParser extends AbstractBsonParser {
	
	private static final String ENUM_NAME = "_enumValue";

	ObjectParser(SmofParser parser) {
		super(parser);
	}
	
	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		
		if(isElement(type)) {
			return fromElement((Element) value);
		}
		else if(isMap(type)) {
			final SmofType valueType = getMapValueType(fieldOpts);
			return fromMap((Map<?, ?>) value, valueType);
		}
		else if(isEnum(type)) {
			return fromEnum((Enum<?>) value);
		}
		else if(isArray(type)) {
			return fromArray((Object[]) value);
		}
		return fromObject(value);
	}

	private BsonValue fromElement(Element value) {
		final ObjectId id = value.getId();
		return new BsonObjectId(id);
	}

	private BsonDocument fromObject(Object value) {
		final BsonDocument document = new BsonDocument();
		final AnnotationParser<?> metadata = getAnnotationParser(value.getClass());
		
		for(SmofField field : metadata.getAllFields()) {
			final Object fieldValue = extractValue(value, field);
			final BsonValue parsedValue;
			
			checkRequired(field, fieldValue);
			parsedValue = bsonParser.toBson(fieldValue, field);
			document.put(field.getName(), parsedValue);
		}
		return document;
	}
	
	private Object extractValue(Object element, SmofField field) {
		try {
			final Field rawField = field.getRawField();
			final Object value;
			rawField.setAccessible(true);
			value = rawField.get(element);
			rawField.setAccessible(false);
			return value;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			handleError(e);
			return null;
		}
	}
	
	private void checkRequired(SmofField field, Object value) {
		if(field.isRequired() && value == null) {
			final String name = field.getName();
			handleError(new MissingRequiredFieldException(name));
		}
	}

	private BsonDocument fromArray(Object[] values) {
		final Map<String, Object> map = new LinkedHashMap<>();
		for(int i=0; i<values.length; i++) {
			map.put(i+"", values[i]);
		}
		return fromMap(map, SmofType.STRING);
	}

	private BsonDocument fromEnum(Enum<?> value) {
		final BsonDocument document = fromObject(value);
		final BsonString name = new BsonString(value.name());
		document.append(ENUM_NAME, name);
		return document;
	}

	private BsonDocument fromMap(Map<?, ?> value, SmofType mapType) {
		final BsonDocument document = new BsonDocument();
		for(Object key : value.keySet()) {
			final Object mapValue = value.get(key);
			final BsonValue parsedValue = bsonParser.toBson(mapValue, mapType);
			document.append(key.toString(), parsedValue);
		}
		return document;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		if(isElement(type)) {
			return null;
		}
		else if(isMap(type)) {
			return (T) toMap(value.asDocument(), fieldOpts);
		}
		else {
			return toObject(value.asDocument(), type);
		}
	}

	private Map<?, ?> toMap(BsonDocument document, SmofField fieldOpts) {
		final Map<Object, Object> map = new LinkedHashMap<>();
		final Pair<Class<?>, Class<?>> mapClass = getMapTypes(fieldOpts.getRawField().getType());
		
		for(String bsonKey : document.keySet()) {
			final BsonValue bsonValue = document.get(bsonKey);
			final Object key = toMapKey(bsonKey, mapClass.getKey());
			final Object value = toMapValue(bsonValue, mapClass.getValue(), fieldOpts);
			map.put(key, value);
		}
		
		return map;
	}
	
	private Object toMapValue(BsonValue value, Class<?> valueType, SmofField fieldOpts) {
		final SmofType smofValueType = getMapValueType(fieldOpts);
		return bsonParser.fromBson(value, valueType, smofValueType);
	}
	
	private Object toMapKey(String key, Class<?> keyType) {
		final BsonString bsonKey = new BsonString(key);
		return bsonParser.fromBson(bsonKey, keyType, SmofType.STRING);
	}

	private <T> T toObject(BsonDocument document, Class<T> type) {
		final BsonBuilder<T> builder = new BsonBuilder<T>();
		final AnnotationParser<T> fields = getAnnotationParser(type);
		for(SmofField field : fields.getAllFields()) {
			final BsonValue fieldValue = document.get(field.getName());
			final Object parsedObj;
			
			checkRequired(field, fieldValue);
			parsedObj = bsonParser.fromBson(fieldValue, field);
			builder.append(field, parsedObj);
		}
		return builder.build(fields);
	}

	@Override
	public boolean isValidType(SmofField fieldOpts) {
		final Class<?> type = fieldOpts.getFieldClass();
		//TODO support enums!
		//TODO support arrays!
		return isValidType(type) && isValidMap(type, fieldOpts);
	}

	@Override
	public boolean isValidType(Class<?> type) {
		return notPrimitive(type);
	}

	private boolean isValidMap(Class<?> type, SmofField fieldOpts) {
		final Pair<Class<?>, Class<?>> mapTypes;
		final SmofType valueType;
		
		checkValidMapOpts(fieldOpts);
		if(type.equals(Map.class)) {
			valueType = getMapValueType(fieldOpts);
			mapTypes = getMapTypes(type);
			return (mapTypes.getKey().isEnum() || mapTypes.getKey().equals(String.class))
					&& bsonParser.isValidType(valueType, mapTypes.getValue());
		}
		return true;
	}
	
	private SmofType getMapValueType(SmofField fieldOpts) {
		final SmofObject note = fieldOpts.getSmofAnnotationAs(SmofObject.class);
		return note.mapValueType();
	}
	
	private Pair<Class<?>, Class<?>> getMapTypes(Class<?> mapClass) {
		final ParameterizedType mapParamType = (ParameterizedType) mapClass.getGenericSuperclass();
		final Class<?> keyClass = (Class<?>) mapParamType.getActualTypeArguments()[0];
		final Class<?> valueClass = (Class<?>) mapParamType.getActualTypeArguments()[1];
		return Pair.of(keyClass, valueClass);
	}

	private void checkValidMapOpts(SmofField fieldOpts) {
		if(fieldOpts == null) {
			handleError(new UnsupportedOperationException("Nested maps are not supported"));
		}
	}

	private boolean notPrimitive(Class<?> type) {
		return !type.isPrimitive();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return value.isDocument() || value.isObjectId();
	}

}
