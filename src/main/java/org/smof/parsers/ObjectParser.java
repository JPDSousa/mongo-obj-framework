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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonValue;
import org.smof.annnotations.SmofObject;
import org.smof.bson.codecs.object.SmofObjectCodecProvider;
import org.smof.collection.SmofDispatcher;
import org.smof.field.PrimaryField;
import org.smof.field.SecondaryField;
import org.smof.field.SmofField;

class ObjectParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {};

	ObjectParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, new SmofObjectCodecProvider(parser), VALID_TYPES);
	}

	@Override
	public boolean isValidType(SmofField fieldOpts) {
		final Class<?> type = fieldOpts.getFieldClass();
		return isValidType(type) && 
				(!isPrimaryField(fieldOpts) || isValidMap(type, (PrimaryField) fieldOpts));
	}

	@Override
	public boolean isValidType(Class<?> type) {
		return !isPrimitive(type) && !isArray(type);
	}

	private boolean isValidMap(Class<?> type, PrimaryField fieldOpts) {
		final Pair<SecondaryField, SecondaryField> mapTypes;

		if(isMap(type)) {
			mapTypes = getMapFields(fieldOpts);

			return String.class.equals(mapTypes.getKey().getFieldClass())
					&& topParser.isValidType(mapTypes.getValue());
		}
		return true;
	}

	private SmofType getMapValueType(PrimaryField fieldOpts) {
		final SmofObject note = fieldOpts.getSmofAnnotationAs(SmofObject.class);
		return note.mapValueType();
	}

	private Pair<SecondaryField, SecondaryField> getMapFields(PrimaryField mapMetadata) {
		final SmofObject annotation = mapMetadata.getSmofAnnotationAs(SmofObject.class);
		final String name = mapMetadata.getName();
		final Field mapField = mapMetadata.getRawField();
		final SmofType valueType = getMapValueType(mapMetadata);
		final ParameterizedType mapParamType = (ParameterizedType) mapField.getGenericType();
		final Class<?> keyClass = (Class<?>) mapParamType.getActualTypeArguments()[0];
		final Class<?> valueClass = (Class<?>) mapParamType.getActualTypeArguments()[1];
		final SecondaryField keyMetadata = new SecondaryField(name, SmofType.STRING, keyClass, annotation);
		final SecondaryField valueMetadata = new SecondaryField(name, valueType, valueClass, annotation);
		
		return Pair.of(keyMetadata, valueMetadata);
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) 
				|| value.isDocument() 
				|| value.isArray() 
				|| value.isObjectId();
	}

}
