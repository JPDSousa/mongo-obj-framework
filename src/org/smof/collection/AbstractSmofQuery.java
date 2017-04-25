package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.smof.element.Element;
import org.smof.field.SmofField;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public abstract class AbstractSmofQuery<T extends Element, Query extends FilterQuery<T, ?>> implements FilterQuery<T, Query> {

	private final BsonDocument filter;
	private final SmofParser parser;
	private final Class<T> elementClass;
	
	protected AbstractSmofQuery(SmofParser parser, Class<T> elementClass) {
		this.filter = new BsonDocument();
		this.elementClass = elementClass;
		this.parser = parser;
	}
	
	@Override
	public Class<T> getElementClass() {
		return elementClass;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query withField(String fieldName, Object filterValue) {
		final SmofField field = parser.getField(elementClass, fieldName);
		final BsonValue bsonValue = parser.toBson(filterValue, field);
		filter.append(fieldName, bsonValue);
		return (Query) this;
	}

	protected BsonDocument getFilter() {
		return filter;
	}

	protected SmofParser getParser() {
		return parser;
	}
}
