package org.smof.collection;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.smof.element.Element;

import com.mongodb.client.model.Filters;

@SuppressWarnings("javadoc")
public class AndQuery<T extends Element> implements FilterQuery<AndQuery<T>> {

	private final SmofQuery<T> parent;
	private final List<Bson> filters;

	AndQuery(SmofQuery<T> parent) {
		this.parent = parent;
		filters = new ArrayList<>();
	}
	
	@Override
	public AndQuery<T> applyBsonFilter(Bson filter) {
		filters.add(filter);
		return this;
	}
	
	@Override
	public AndQuery<T> withField(String fieldName, Object filterValue) {
		//validateFieldValue(fieldName, filterValue);
		filters.add(Filters.eq(fieldName, filterValue));
		return this;
	}
	
	public SmofQuery<T> endAnd() {
		parent.applyBsonFilter(Filters.and(filters));
		return parent;
	}
	
}
