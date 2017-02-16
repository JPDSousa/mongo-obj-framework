package org.smof.element;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNumber;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.smof.element.field.SmofArray;
import org.smof.element.field.SmofField;
import static org.smof.element.field.SmofField.FieldType;
import org.smof.exception.InvalidTypeException;
import org.smof.exception.MissingRequiredFieldException;
import org.smof.exception.NoSuchAdapterException;
import org.smof.exception.UnsupportedBsonException;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public class SmofAdapter<T> {

	private final SmofFactory<T> factory;
	private final SmofAdapterPool adapters;
	private final SmofAnnotationParser<T> parser;

	public SmofAdapter(SmofFactory<T> factory, SmofAnnotationParser<T> parser, SmofAdapterPool adapters) {
		this.factory = factory;
		this.adapters = adapters;
		this.parser = parser;
	}

	public Class<T> getType() {
		return parser.getType();
	}

	public SmofAnnotationParser<T> getSmofMetadata() {
		return parser;
	}

	public T read(BsonDocument document) {
		try {
			final T element = factory.createSmofObject(document);
			BsonValue value;
			Object obj = null;

			for(SmofField field : parser.getFields()) {
				value = document.get(field.getName());
				switch(field.getType()) {
				case ARRAY:
					obj = getArray(field, value);
					break;
				case DATE:
					obj = getDate(field, value);
					break;
				case NUMBER:
					obj = getNumber(field, value);
					break;
				case OBJECT:
					obj = getObject(field, value);
					break;
				case OBJECT_ID:
					obj = getObjectId(value);
					break;
				case STRING:
					obj = getString(field, value);
					break;
				}
				field.getField().setAccessible(true);
				field.getField().set(element, obj);
				field.getField().setAccessible(false);
			}
			return element;
		} catch (IllegalArgumentException | IllegalAccessException | SmofException | UnsupportedBsonException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Object getString(SmofField field, BsonValue rawValue) throws UnsupportedBsonException {
		if(rawValue.isString()) {
			final BsonString value = rawValue.asString();
			if(field.getField().getType().equals(String.class)) {
				return value.getValue();
			}
			else if(field.getField().getType().isEnum()) {
				try {
					final Method valueOf = field.getField().getType().getMethod("valueOf", String.class);
					return valueOf.invoke(null, value.getValue());
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					//all enums have valueOf!!
					e.printStackTrace();
					return null;
				}
			}
		}
		throw new UnsupportedBsonException();
	}

	private Object getObjectId(BsonValue value) throws UnsupportedBsonException {
		if(value.isObjectId()) {
			return value.asObjectId().getValue();
		}
		throw new UnsupportedBsonException();
	}

	private Object getObject(SmofField field, BsonValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getNumber(SmofField field, BsonValue rawValue) throws UnsupportedBsonException {
		if(rawValue.isNumber()) {
			final BsonNumber numberValue = rawValue.asNumber();
			final Class<?> type = field.getField().getType();
			if(numberValue.isInt32() && type.equals(Integer.class)) {
				return new Integer(numberValue.asInt32().getValue());
			}
			else if(numberValue.isInt32() && type.equals(Short.class)) {
				final int value = numberValue.asInt32().getValue();
				if(value > Short.MIN_VALUE && value < Short.MAX_VALUE) {
					return new Short((short) numberValue.asInt32().getValue());					
				}
			}
			else if(numberValue.isDouble() && type.equals(Float.class)) {
				final double value = numberValue.asDouble().getValue();
				if(value < Float.MAX_VALUE && value > Float.MIN_VALUE) {
					return new Float((float) value);
				}
			}
			else if(numberValue.isDouble() && type.equals(Double.class)) {
				return new Double(numberValue.asDouble().getValue());
			}
			else if(numberValue.isInt64() && type.equals(Long.class)) {
				return new Long(numberValue.asInt64().getValue());
			}
		}
		throw new UnsupportedBsonException();
	}

	private Object getDate(SmofField field, BsonValue value) throws UnsupportedBsonException {
		final Class<?> fieldClass = field.getField().getType();
		final BsonDateTime date;
		if(value.isDateTime()) {
			date = value.asDateTime();
			if(fieldClass.equals(Instant.class)) {
				return Instant.ofEpochMilli(date.getValue());
			}
			else if(fieldClass.equals(LocalDateTime.class)) {
				return LocalDateTime.ofInstant(
						Instant.ofEpochMilli(date.getValue()), 
						ZoneId.systemDefault());
			}
			else if(fieldClass.equals(LocalDate.class)) {
				return LocalDateTime.ofInstant(
						Instant.ofEpochMilli(date.getValue()), 
						ZoneId.systemDefault())
						.toLocalDate();
			}
		}
		throw new UnsupportedBsonException();
	}

	private Object getArray(SmofField field, BsonValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	public BsonDocument write(T element) throws SmofException {
		return writeObject(element);
	}

	private BsonArray handleArray(Object fieldValue, SmofArray annotation) throws SmofException {
		final BsonArray array = new BsonArray();
		switch(annotation.type()) {
		case ARRAY:
			throw new SmofException(new UnsupportedBsonException("Arrays of arrays are not supported yet."));
		case DATE:
			for(BsonDateTime date : parseDateArray(parseArray(fieldValue))) {
				array.add(date);
			}
			break;
		case NUMBER:
			for(BsonNumber number : parseNumberArray(parseArray(fieldValue))) {
				array.add(number);
			}
			break;
		case OBJECT:
			break;
		case OBJECT_ID:
			break;
		case STRING:
			break;
		default:
			break;

		}
		return array;
	}

	private Object[] parseArray(Object fieldValue) throws SmofException {
		if(fieldValue instanceof Collection) {
			final Collection<?> col = Collection.class.cast(fieldValue);
			return col.toArray(new Object[col.size()]);
		}
		else if(fieldValue instanceof Object[]) {
			return Object[].class.cast(fieldValue);
		}
		else if(fieldValue.getClass().isArray() && fieldValue.getClass().getComponentType().isPrimitive()) {
			Object[] array = new Object[Array.getLength(fieldValue)];
			for(int i = 0; i < array.length; i++) {
				array[i] = Array.get(fieldValue, i);
			}
			return array;
		}
		throw new SmofException(new InvalidTypeException(fieldValue.getClass(), FieldType.ARRAY));
	}

	private List<BsonNumber> parseNumberArray(Object[] array) throws SmofException {
		boolean valid = true;
		Class<?> currentClass = null;
		final List<BsonNumber> bsonNumbers = new ArrayList<>();

		for(int i = 0; valid && i < array.length && (currentClass == null || currentClass == array[i].getClass()); i++) {
			if(array[i] instanceof Number) {
				currentClass = array[i].getClass();
				if(array[i] instanceof Integer) {
					bsonNumbers.add(new BsonInt32(Integer.class.cast(array[i])));
				} 
				else if(array[i] instanceof Short) {
					bsonNumbers.add(new BsonInt32(Short.class.cast(array[i])));
				}
				else if(array[i] instanceof Float) {
					bsonNumbers.add(new BsonDouble(Float.class.cast(array[i])));
				}
				else if(array[i] instanceof Double) {
					bsonNumbers.add(new BsonDouble(Double.class.cast(array[i])));
				}
				else if(array[i] instanceof Long) {
					bsonNumbers.add(new BsonInt64(Long.class.cast(array[i])));
				}
			}
			else {
				valid = false;
			}
		}
		if(valid) {
			return bsonNumbers;
		}

		throw new SmofException(new InvalidTypeException(array.getClass(), FieldType.ARRAY));
	}

	private List<BsonDateTime> parseDateArray(Object[] rawArray) throws SmofException {
		if(rawArray instanceof Instant[]) {
			return Arrays.stream(Instant[].class.cast(rawArray))
					.map(i -> new BsonDateTime(i.toEpochMilli()))
					.collect(Collectors.toList());
		}
		else if (rawArray instanceof LocalDateTime[]) {
			return Arrays.stream(LocalDateTime[].class.cast(rawArray))
					.map(d -> d.atZone(ZoneId.systemDefault()).toInstant())
					.map(i -> new BsonDateTime(i.toEpochMilli()))
					.collect(Collectors.toList());
		}
		else if (rawArray instanceof LocalDate[]) {
			return Arrays.stream(LocalDate[].class.cast(rawArray))
					.map(d -> d.atStartOfDay(ZoneId.systemDefault()).toInstant())
					.map(i -> new BsonDateTime(i.toEpochMilli()))
					.collect(Collectors.toList());
		}
		throw new SmofException(new InvalidTypeException(rawArray.getClass(), FieldType.ARRAY));
	}

	private BsonDateTime handleDate(Object fieldValue) throws SmofException {
		if(fieldValue instanceof Instant) {
			return new BsonDateTime(Instant.class.cast(fieldValue).toEpochMilli());
		}
		else if(fieldValue instanceof LocalDateTime) {
			return new BsonDateTime(LocalDateTime.class.cast(fieldValue).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		else if(fieldValue instanceof LocalDate) {
			return new BsonDateTime(LocalDate.class.cast(fieldValue).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		throw new SmofException(new InvalidTypeException(fieldValue.getClass(), FieldType.DATE));
	}

	private BsonValue handleObject(Object obj) throws SmofException {
		if(obj instanceof Element) {
			return new BsonObjectId(Element.class.cast(obj).getId());
		}
		else if(obj instanceof Map) {
			final Map<?, ?> map = Map.class.cast(obj);
			final BsonDocument document = new BsonDocument();
			for(Object key : map.keySet()) {
				document.append(key.toString(), handleObject(map.get(key)));
			}
			return document;
		}
		return writeObject(obj);
	}

	private BsonDocument writeObject(Object obj) throws SmofException {
		try {
			if(!obj.getClass().equals(parser.getType())) {
				return adapters.get(obj.getClass()).writeObject(obj);
			}
			final BsonDocument document = new BsonDocument();
			BsonValue value;
			Object fieldValue;

			for(SmofField smofField : parser.getFields()) {
				smofField.getField().setAccessible(true);
				fieldValue = smofField.getField().get(obj);
				if(smofField.isRequired() && fieldValue == null)
					throw new SmofException(new MissingRequiredFieldException(smofField.getName()));

				switch(smofField.getType()) {
				case ARRAY:
					value = handleArray(fieldValue, smofField.getSmofAnnotationAs(SmofArray.class));
					break;
				case DATE:
					value = handleDate(fieldValue);
					break;
				case NUMBER:
					value = handleNumber(fieldValue);
					break;
				case OBJECT:
					value = handleObject(fieldValue);
					break;
				case OBJECT_ID:
					value = handleObjectId(fieldValue);
					break;
				case STRING:
					value = handleString(fieldValue);
					break;
				default:
					throw new UnsupportedBsonException();
				}
				smofField.getField().setAccessible(false);
				document.append(smofField.getName(), value);
			}
			return document;
		} catch(IllegalAccessException | UnsupportedBsonException | NoSuchAdapterException e){
			throw new SmofException(e);
		}
	}

	private BsonObjectId handleObjectId(Object fieldValue) {
		return new BsonObjectId(ObjectId.class.cast(fieldValue));
	}

	private BsonNumber handleNumber(Object fieldValue) throws SmofException {
		if(fieldValue instanceof Integer) {
			return new BsonInt32(Integer.class.cast(fieldValue));
		} 
		else if(fieldValue instanceof Short) {
			return new BsonInt32(Short.class.cast(fieldValue));
		}
		else if(fieldValue instanceof Float) {
			return new BsonDouble(Float.class.cast(fieldValue));
		}
		else if(fieldValue instanceof Double) {
			return new BsonDouble(Double.class.cast(fieldValue));
		}
		else if(fieldValue instanceof Long) {
			return new BsonInt64(Long.class.cast(fieldValue));
		}
		throw new SmofException(new InvalidTypeException(fieldValue.getClass(), FieldType.NUMBER));
	}

	private BsonString handleString(Object fieldValue) {
		if(fieldValue instanceof Enum<?>) {
			return new BsonString(Enum.class.cast(fieldValue).name());
		} 
		return new BsonString(fieldValue.toString());
	}

}