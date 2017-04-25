package org.smof.collection;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.smof.element.Element;

import com.mongodb.client.model.Filters;

@SuppressWarnings("javadoc")
public class OrQuery<T extends Element> extends AbstractSmofQuery<T, OrQuery<T>> {

	private final SmofQuery<T> parent;
	private final List<Bson> filters;

	OrQuery(SmofQuery<T> parent) {
		super(parent.getParser(), parent.getElementClass());
		this.parent = parent;
		filters = new ArrayList<>();
	}
	
	public OrQuery<T> or(Bson filter) {
		filters.add(filter);
		return this;
	}
	
	@Override
	public OrQuery<T> applyBsonFilter(Bson filter) {
		return or(filter);
	}
	
	public SmofQuery<T> endOr() {
		parent.applyBsonFilter(Filters.or(filters));
		return parent;
	}

	@Override
	public SmofResults<T> results() {
		return endOr().results();
	}

}
