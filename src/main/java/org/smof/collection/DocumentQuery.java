package org.smof.collection;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.smof.element.Element;

@SuppressWarnings("javadoc")
public class DocumentQuery<T extends Element> extends AbstractChildQuery<T, DocumentQuery<T>> {

	private final BsonDocument filter;
	
	DocumentQuery(AbstractQuery<T, SmofQuery<T, ?>> parent, QueryOperators op) {
		super(parent, op);
		filter = new BsonDocument();
	}

	@Override
	protected void append(String name, BsonValue value) {
		filter.append(name, value);
	}

	@Override
	protected BsonValue getFilter() {
		return filter;
	}

}
