package org.smof.collection;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.smof.element.Element;

import com.mongodb.client.model.Filters;

@SuppressWarnings("javadoc")
public class OrQuery<T extends Element> implements FilterQuery<OrQuery<T>> {

	private final SmofQuery<T> parent;
	private final List<Bson> filters;

	OrQuery(SmofQuery<T> parent) {
		this.parent = parent;
		filters = new ArrayList<>();
	}
	
	@Override
	public OrQuery<T> applyBsonFilter(Bson filter) {
		filters.add(filter);
		return this;
	}
	
	@Override
	public OrQuery<T> withField(String fieldName, Object filterValue) {
		//validateFieldValue(fieldName, filterValue);
		filters.add(Filters.eq(fieldName, filterValue));
		return this;
	}
	
	public SmofQuery<T> endOr() {
		parent.applyBsonFilter(Filters.or(filters));
		return parent;
	}

}
