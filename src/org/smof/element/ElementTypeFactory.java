package org.smof.element;

import java.io.IOException;
import java.lang.reflect.Field;

import java.time.Instant;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import org.smof.element.field.Fields;
import org.smof.element.field.SmofArray;
import org.smof.element.field.SmofDate;
import org.smof.element.field.SmofInnerObject;
import org.smof.element.field.SmofNumber;
import org.smof.element.field.SmofObjectId;
import org.smof.element.field.SmofString;
import org.smof.exception.MissingRequiredFieldException;
import org.smof.exception.NotSupportedException;
import org.smof.exception.SmofException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings("javadoc")
public class ElementTypeFactory implements TypeAdapterFactory {

	private static ElementTypeFactory singleton;

	public static ElementTypeFactory getDefault() {
		if(singleton == null) {
			singleton = new ElementTypeFactory();
		}

		return singleton;
	}

	private ElementTypeFactory() {}

	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Gson arg0, TypeToken<T> arg1) {
		if(!Element.class.isAssignableFrom(arg1.getRawType())) {
			return null;
		}
		return (TypeAdapter<T>) new ElementAdapter();
	}

	private class ElementAdapter extends TypeAdapter<Element> {

		private final ElementParser parser;

		public ElementAdapter() {
			parser = ElementParser.getDefault();
		}

		@Override
		public Element read(JsonReader reader) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void write(JsonWriter writer, Element element) throws IOException {
			writeObject(writer, element, null);
		}

		private void writeArray(JsonWriter writer, Object fieldValue, SmofArray annotation) {
			// TODO Auto-generated method stub

		}

		private void writeDate(JsonWriter writer, Object fieldValue, SmofDate annotation) throws IOException {
			final StringBuilder dateBuilder = new StringBuilder("ISODate(\"");
			dateBuilder.append(Instant.class.cast(fieldValue).toString());
			dateBuilder.append("\")");
			writer.name(annotation.name());
			writer.jsonValue(dateBuilder.toString());
		}

		private void writeObject(JsonWriter writer, Object obj, SmofInnerObject annotation) throws IOException {
			try {
				if(annotation != null) {
					if(annotation.required() && obj == null)
						throw new SmofException(new MissingRequiredFieldException(annotation.name()));
					writer.name(annotation.name());
				}
				final Map<Fields, Field> fields = parser.getSmofFields(obj.getClass());
				Field field;
				Object fieldValue;

				writer.beginObject();
				for(Fields fieldType : fields.keySet()) {
					field = fields.get(fieldType);
					field.setAccessible(true);
					fieldValue = field.get(obj);

					switch(fieldType) {
					case ARRAY:
						writeArray(writer, fieldValue, field.getAnnotation(SmofArray.class));
						break;
					case DATE:
						writeDate(writer, fieldValue, field.getAnnotation(SmofDate.class));
						break;
					case NUMBER:
						writeNumber(writer, fieldValue, field.getAnnotation(SmofNumber.class));
						break;
					case OBJECT:
						writeObject(writer, fieldValue, field.getAnnotation(SmofInnerObject.class));
						break;
					case OBJECT_ID:
						writeObjectId(writer, fieldValue, field.getAnnotation(SmofObjectId.class));
						break;
					case STRING:
						writeString(writer, fieldValue, field.getAnnotation(SmofString.class));
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

		private void writeObjectId(JsonWriter writer, Object fieldValue, SmofObjectId annotation) throws IOException, SmofException {
			if(annotation.required() && fieldValue == null)
				throw new SmofException(new MissingRequiredFieldException(annotation.name()));
			final StringBuilder objectIdBuilder = new StringBuilder("ObjectId(\"");
			objectIdBuilder.append(ObjectId.class.cast(fieldValue).toHexString()).append("\")");
			writer.name(annotation.name());
			writer.jsonValue(objectIdBuilder.toString());
		}

		private void writeNumber(JsonWriter writer, Object fieldValue, SmofNumber annotation) throws IOException {
			writer.name(annotation.name());
			writer.value(Number.class.cast(fieldValue));
		}

		private void writeString(JsonWriter writer, Object fieldValue, SmofString annotation) throws IOException {
			writer.name(annotation.name());
			writer.value(String.class.cast(fieldValue));
		}

	}

}
