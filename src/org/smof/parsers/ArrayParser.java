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
import org.smof.field.PrimaryField;
import org.smof.field.SecondaryField;
import org.smof.field.SmofField;

class ArrayParser extends AbstractBsonParser {

	ArrayParser(SmofParser parser) {
		super(parser, Collection.class);
	}

	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		if(isPrimaryField(fieldOpts) && isCollection(type)) {
			final Object[] array;
			final SecondaryField componentField = getCollectionField((PrimaryField) fieldOpts);
			array = fromCollection((Collection<?>) value);
			return fromArray(array, componentField);
		}
		return null;
	}

	private BsonValue fromArray(Object[] values, SecondaryField componentField) {
		final BsonArray bsonArray = new BsonArray();
		for(Object value : values) {
			final BsonValue parsedValue = bsonParser.toBson(value, componentField);
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
		if(isCollection(type) && isPrimaryField(fieldOpts)) {
			return (T) toCollection(value, (PrimaryField) fieldOpts);
		}

		return null;
	}

	private Collection<Object> toCollection(BsonArray values, PrimaryField fieldOpts) {
		final SecondaryField componentField = getCollectionField(fieldOpts);
		final Collection<Object> collection = createCollection(fieldOpts.getFieldClass(), componentField.getFieldClass());
		for(BsonValue value : values) {
			final Object parsedValue = bsonParser.fromBson(value, componentField);
			collection.add(parsedValue);
		}

		return collection;
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
				&& (!isPrimaryField(fieldOpts) || isValidComponentType((PrimaryField) fieldOpts));
	}

	private boolean isValidComponentType(PrimaryField fieldOpts) {
		//Careful here! The next line is only safe 'cause we only support collections
		final SecondaryField componentField = getCollectionField(fieldOpts);
		final Class<?> componentClass = componentField.getFieldClass();
		final SmofType componentType = componentField.getType();

		return isSupportedComponentType(componentType)
				&& !isMap(componentClass)
				&& bsonParser.isValidType(componentField);
	}

	private boolean isSupportedComponentType(SmofType componentType) {
		return componentType != SmofType.ARRAY;
	}

	private SecondaryField getCollectionField(PrimaryField fieldOpts) {
		final SmofArray note = fieldOpts.getSmofAnnotationAs(SmofArray.class);
		final Class<?> componentClass = getCollectionType(fieldOpts.getRawField());
		return new SecondaryField(fieldOpts.getName(), note.type(), componentClass);
	}

	private Class<?> getCollectionType(Field collType) {
		final ParameterizedType mapParamType = (ParameterizedType) collType.getGenericType();
		return (Class<?>) mapParamType.getActualTypeArguments()[0];
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) || value.isArray();
	}
}
