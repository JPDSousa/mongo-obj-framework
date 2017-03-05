package org.smof.collection;

import static org.smof.collection.Operators.*;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class SmofUpdate<T extends Element> {
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private BsonDocument update;
	private final SmofParser parser;
	private final Map<String, PrimaryField> fields;
	private final Class<T> type;
	private final SmofCollection<T> collection;
	private final SmofUpdateOptions options;
	
	SmofUpdate(SmofCollection<T> collection) {
		update = new BsonDocument();
		this.parser = collection.getParser();
		this.type = collection.getType();
		this.collection = collection;
		fields = parser.getTypeStructure(type).getAllFields();
		options = SmofUpdateOptions.create();
	}
	
	private SmofField validateFieldName(String fieldName) {
		final PrimaryField field = fields.get(fieldName);
		if(field == null) {
			handleError(new IllegalArgumentException(fieldName + " is not a valid field name for type " + type.getName()));
		}
		return field;
	}
	
	public SmofUpdate<T> setUpsert(boolean upsert) {
		options.upsert(upsert);
		return this;
	}
	
	public SmofUpdateQuery<T> where() {
		return new SmofUpdateQuery<>(update, collection, options, fields);
	}
	
	public void fromElement(T element) {
		collection.replace(element, options);
	}

	public SmofUpdate<T> increase(Number value, String fieldName) {
		final SmofField field = validateFieldName(fieldName);
		final BsonValue number = parser.toBson(value, field);
		final BsonDocument doc = new BsonDocument();
		doc.put(fieldName, number);
		putOrAppend(INCREASE.getOperator(), doc);
		return this;
	}
	
	public SmofUpdate<T> multiply(Number value, String fieldName) {
		final SmofField field = validateFieldName(fieldName);
		final BsonValue number = parser.toBson(value, field);
		final BsonDocument doc = new BsonDocument();
		doc.put(fieldName, number);
		putOrAppend(MULTIPLY.getOperator(), doc);
		return this;
	}
	
	public SmofUpdate<T> rename(String newName, String fieldName) {
		validateFieldName(fieldName);
		final BsonDocument doc = new BsonDocument();
		doc.put(fieldName, new BsonString(newName));
		putOrAppend(RENAME.getOperator(), doc);
		return this;
	}

	private void putOrAppend(String key, BsonDocument doc) {
		if(update.containsKey(key)) {
			update.getDocument(key).putAll(doc);
		}
		else {
			update.put(key, doc);
		}
	}
}
