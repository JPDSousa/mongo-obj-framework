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
import org.smof.collection.SmofDispatcher;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;
import org.smof.field.SecondaryField;
import org.smof.field.SmofField;

class ArrayParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {Collection.class};
	
	ArrayParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, null, VALID_TYPES);
	}

	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		final SerializationContext serContext = bsonParser.getSerializationContext();
		if(serContext.contains(value, fieldOpts.getType())) {
			return serContext.get(value, fieldOpts.getType());
		}
		final Class<?> type = value.getClass();
		if(isPrimaryField(fieldOpts) && isCollection(type)) {
			final Object[] array;
			final SecondaryField componentField = getCollectionField((PrimaryField) fieldOpts);
			array = fromCollection((Collection<?>) value);
			return fromArray(array, componentField);
		}
		return null;
	}

	@Override
	protected BsonValue serializeToBson(Object value, SmofField fieldOpts) {
		// unused
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
		if(isCollection(type)) {
			if(isPrimaryField(fieldOpts)) {
				return (T) toCollection(value, (PrimaryField) fieldOpts);
			}
			else if(isParameterField(fieldOpts)) {
				return (T) toCollection(value, ((ParameterField) fieldOpts).getPrimaryField());
			}
		}
		return null;
	}

	private Collection<Object> toCollection(BsonArray values, PrimaryField fieldOpts) {
		final SecondaryField componentField = getCollectionField(fieldOpts);
		final Collection<Object> collection = createCollection(fieldOpts.getFieldClass());
		for(BsonValue value : values) {
			final Object parsedValue = bsonParser.fromBson(value, componentField);
			collection.add(parsedValue);
		}

		return collection;
	}

	@SuppressWarnings("unchecked")
	private <T> Collection<Object> createCollection(Class<?> collectionClass) {
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
