package org.smof.collection;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class SmofUpdateQuery<T extends Element> {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private final BsonDocument update;
	private final SmofCollection<T> collection;
	private BsonDocument filter;
	private final SmofUpdateOptions options;
	private final SmofParser parser;
	private final Map<String, PrimaryField> fields;
	private final Class<T> type;
	
	SmofUpdateQuery(BsonDocument update, SmofCollection<T> collection, SmofUpdateOptions options, Map<String, PrimaryField> fields) {
		super();
		this.update = update;
		this.collection = collection;
		filter = new BsonDocument();
		this.options = options;
		this.parser = collection.getParser();
		this.fields = fields;
		this.type = collection.getType();
	}
	
	private SmofField validateFieldName(String fieldName) {
		final PrimaryField field = fields.get(fieldName);
		if(field == null) {
			handleError(new IllegalArgumentException(fieldName + " is not a valid field name for type " + type.getName()));
		}
		return field;
	}
	
	public SmofUpdateQuery<T> fieldEq(String fieldName, Object value) {
		final SmofField field = validateFieldName(fieldName);
		filter.append(fieldName, parser.toBson(value, field));
		return this;
	}
	
	public void idEq(ObjectId id) {
		filter = new BsonDocument(Element.ID, new BsonObjectId(id));
		execute();
	}
	
	public void execute() {
		collection.execUpdate(filter, update, options);
		parser.reset();
	}
	
}
