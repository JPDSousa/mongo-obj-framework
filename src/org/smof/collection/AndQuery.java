package org.smof.collection;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.smof.element.Element;

import com.mongodb.client.model.Filters;

@SuppressWarnings("javadoc")
public class AndQuery<T extends Element> extends AbstractSmofQuery<T, AndQuery<T>> {

	private final SmofQuery<T> parent;
	private final List<Bson> filters;

	AndQuery(SmofQuery<T> parent) {
		super(parent.getParser(), parent.getElementClass());
		this.parent = parent;
		filters = new ArrayList<>();
	}
	
	public AndQuery<T> and(Bson filter) {
		filters.add(filter);
		return this;
	}
	
	public SmofQuery<T> endAnd() {
		parent.applyBsonFilter(Filters.and(filters));
		return parent;
	}

	@Override
	public AndQuery<T> applyBsonFilter(Bson filter) {
		return and(filter);
	}

	@Override
	public SmofResults<T> results() {
		return endAnd().results();
	}
	
}
