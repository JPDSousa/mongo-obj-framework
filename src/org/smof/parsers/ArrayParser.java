package org.smof.parsers;

import java.util.Collection;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofField;

class ArrayParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {Object[].class, Collection.class};
	
	ArrayParser(SmofParser parser) {
		super(parser, VALID_TYPES);
	}
	
	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final Class<?> type = value.getClass();
		final Object[] array;
		final SmofType componentType = getArrayType(fieldOpts);
		if(isObjectArray(type)) {
			array = (Object[]) value;
		}
		else if(isCollection(type)) {
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

	private boolean isObjectArray(Class<?> type) {
		return Object[].class.isAssignableFrom(type);
	}

	@Override
	public <T> T fromBson(BsonValue rawValue, Class<T> type) {
//		BsonArray value = rawValue.asArray();
//		Object[] array = toArray(value);
//		if(isObjectArray(type)) {
//			
//		}
		
		return null;
	}

	@Override
	public boolean isValidType(Class<?> type, SmofField fieldOpts) {
		return super.isValidType(type, fieldOpts)
				&& isValidComponentType(type, fieldOpts);
	}

	private boolean isValidComponentType(Class<?> type, SmofField fieldOpts) {
		final SmofType componentType = getArrayType(fieldOpts);
		final Class<?> componentClass = type.getComponentType();
		return isSupportedComponentType(componentType)
				&& !isMap(componentClass)
				&& bsonParser.isValidType(componentType, componentClass);
	}

	private boolean isSupportedComponentType(SmofType componentType) {
		return componentType != SmofType.ARRAY;
	}

	private SmofType getArrayType(SmofField fieldOpts) {
		return fieldOpts.getSmofAnnotationAs(SmofArray.class).type();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return value.isArray();
	}

}
