package org.smof.element;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import org.bson.types.ObjectId;
import org.smof.element.field.SmofArray;
import org.smof.element.field.SmofDate;
import org.smof.element.field.SmofField;
import org.smof.element.field.SmofInnerObject;
import org.smof.element.field.SmofNumber;
import org.smof.element.field.SmofObjectId;
import org.smof.element.field.SmofString;
import org.smof.exception.InvalidTypeException;
import org.smof.exception.MissingRequiredFieldException;
import org.smof.exception.NotSupportedException;
import org.smof.exception.SmofException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class ElementAdapter extends TypeAdapter<Element> {

	private final ElementParser parser;

	ElementAdapter() {
		parser = ElementParser.getDefault();
	}

	@Override
	public Element read(JsonReader reader) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(JsonWriter writer, Element element) throws IOException {
		handleObject(writer, element, null);
	}

	private void handleArray(JsonWriter writer, Object fieldValue, SmofArray annotation) throws SmofException, IOException {
		if(annotation.required() && fieldValue == null) {
			throw new SmofException(new MissingRequiredFieldException(annotation.name()));
		}
		writer.beginArray();
		switch(annotation.type()) {
		case ARRAY:
			throw new SmofException(new NotSupportedException("Arrays of arrays are not supported yet."));
		case DATE:
			if(fieldValue instanceof Instant[]) {
				for(Instant instant : Instant[].class.cast(fieldValue)) {
					//writer.jsonValue(value)
				}
			}
			else {
				throw new SmofException(new InvalidTypeException(fieldValue.getClass(), SmofField.ARRAY));
			}
			break;
		case NUMBER:
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
		writer.endArray();
	}

	private void handleDate(JsonWriter writer, Object fieldValue, SmofDate annotation) throws IOException, SmofException {
		if(annotation.required() && fieldValue == null) {
			throw new SmofException(new MissingRequiredFieldException(annotation.name()));
		}
		writer.name(annotation.name());
		writeDateValue(writer, parseDate(fieldValue));
	}
	
	private Instant parseDate(Object fieldValue) throws SmofException {
		if(fieldValue instanceof Instant) {
			return Instant.class.cast(fieldValue);
		}
		else if(fieldValue instanceof LocalDateTime) {
			return LocalDateTime.class.cast(fieldValue).atZone(ZoneId.systemDefault()).toInstant();
		}
		else if(fieldValue instanceof LocalDate) {
			return LocalDate.class.cast(fieldValue).atStartOfDay(ZoneId.systemDefault()).toInstant();
		}
		throw new SmofException(new InvalidTypeException(fieldValue.getClass(), SmofField.DATE));
	}

	private void writeDateValue(JsonWriter writer, Instant instant) throws IOException {
		final StringBuilder dateBuilder = new StringBuilder("ISODate(\"");
		dateBuilder.append(instant.toString());
		dateBuilder.append("\")");
		writer.jsonValue(dateBuilder.toString());
	}

	private void handleObject(JsonWriter writer, Object obj, SmofInnerObject annotation) throws IOException {
		try {
			if(annotation != null) {
				if(annotation.required() && obj == null)
					throw new SmofException(new MissingRequiredFieldException(annotation.name()));
				writer.name(annotation.name());
			}
			final Map<SmofField, Field> fields = parser.getSmofFields(obj.getClass());
			Field field;
			Object fieldValue;

			writer.beginObject();
			for(SmofField fieldType : fields.keySet()) {
				field = fields.get(fieldType);
				field.setAccessible(true);
				fieldValue = field.get(obj);

				switch(fieldType) {
				case ARRAY:
					handleArray(writer, fieldValue, field.getAnnotation(SmofArray.class));
					break;
				case DATE:
					handleDate(writer, fieldValue, field.getAnnotation(SmofDate.class));
					break;
				case NUMBER:
					handleNumber(writer, fieldValue, field.getAnnotation(SmofNumber.class));
					break;
				case OBJECT:
					handleObject(writer, fieldValue, field.getAnnotation(SmofInnerObject.class));
					break;
				case OBJECT_ID:
					handleObjectId(writer, fieldValue, field.getAnnotation(SmofObjectId.class));
					break;
				case STRING:
					handleString(writer, fieldValue, field.getAnnotation(SmofString.class));
					break;
				default:
					throw new NotSupportedException();
				}
				field.setAccessible(false);
			}
			writer.endObject();
		} catch(IllegalAccessException | NotSupportedException | SmofException e){
			throw new IOException(e);
		}
	}

	private void handleObjectId(JsonWriter writer, Object fieldValue, SmofObjectId annotation) throws IOException, SmofException {
		if(annotation.required() && fieldValue == null)
			throw new SmofException(new MissingRequiredFieldException(annotation.name()));
		final StringBuilder objectIdBuilder = new StringBuilder("ObjectId(\"");
		objectIdBuilder.append(ObjectId.class.cast(fieldValue).toHexString()).append("\")");
		writer.name(annotation.name());
		writer.jsonValue(objectIdBuilder.toString());
	}

	private void handleNumber(JsonWriter writer, Object fieldValue, SmofNumber annotation) throws IOException {
		writer.name(annotation.name());
		writer.value(Number.class.cast(fieldValue));
	}

	private void handleString(JsonWriter writer, Object fieldValue, SmofString annotation) throws IOException {
		writer.name(annotation.name());
		writer.value(String.class.cast(fieldValue));
	}

}