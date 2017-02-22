package org.smof.parsers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofField;

class ArrayParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {Collection.class};

	ArrayParser(SmofParser parser) {
		super(parser, VALID_TYPES);
	}

	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		final Object[] array;
		final SmofType componentType = getArrayType(fieldOpts);
		if(isCollection(type)) {
			array = fromCollection((Collection<?>) value);
		}
		else {
			array = null;
		}
		return fromArray(array, componentType);
	}

	private BsonValue fromArray(Object[] values, SmofType componentType) {
		final BsonArray bsonArray = new BsonArray();
		for(Object value : values) {
			final BsonValue parsedValue = bsonParser.toBson(value, componentType);
			bsonArray.add(parsedValue);
		}

		return bsonArray;
	}

	private Object[] fromCollection(Collection<?> value) {
		return value.toArray(new Object[value.size()]);
	}

	private boolean isCollection(Class<?> type) {
		return Collection.class.isAssignableFrom(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue rawValue, Class<T> type, SmofField fieldOpts) {
		BsonArray value = rawValue.asArray();
		if(isCollection(type)) {
			return (T) toCollection(value, fieldOpts);
		}

		return null;
	}

	private Collection<Object> toCollection(BsonArray values, SmofField fieldOpts) {
		final SmofType arrayType = getArrayType(fieldOpts);
		final Class<?> type = getCollectionType(fieldOpts.getRawField());
		final Collection<Object> collection = createCollection(fieldOpts.getFieldClass(), type);
		for(BsonValue value : values) {
			final Object parsedValue = bsonParser.fromBson(value, type, arrayType);
			collection.add(parsedValue);
		}

		return collection;
	}

	private Class<?> getCollectionType(Field collType) {
		final ParameterizedType mapParamType = (ParameterizedType) collType.getGenericType();
		return (Class<?>) mapParamType.getActualTypeArguments()[0];
	}

	@SuppressWarnings("unchecked")
	private <T> Collection<Object> createCollection(Class<?> collectionClass, Class<T> type) {
		final Collection<T> collection;
		if(List.class.isAssignableFrom(collectionClass)) {
			collection = new ArrayList<>();
		}
		else if(Set.class.isAssignableFrom(collectionClass)) {
			collection = new LinkedHashSet<>();
		}
		else {
			collection = null;
		}
		return (Collection<Object>) collection;
	}

	@Override
	public boolean isValidType(SmofField fieldOpts) {
		final Class<?> type = fieldOpts.getFieldClass();

		return super.isValidType(type)
				&& isValidComponentType(fieldOpts);
	}

	private boolean isValidComponentType(SmofField fieldOpts) {
		final SmofType componentType = getArrayType(fieldOpts);
		//Careful here! The next line is only safe 'cause we only support collections
		final Class<?> componentClass = getCollectionType(fieldOpts.getRawField());
		return isSupportedComponentType(componentType)
				&& !isMap(componentClass)
				&& bsonParser.isValidType(componentType, componentClass);
	}

	private boolean isSupportedComponentType(SmofType componentType) {
		return componentType != SmofType.ARRAY;
	}

	private SmofType getArrayType(SmofField fieldOpts) {
		final SmofArray note = fieldOpts.getSmofAnnotationAs(SmofArray.class);
		return note.type();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) || value.isArray();
	}
}
