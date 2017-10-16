package org.smof.collection;

import org.smof.element.Element;

abstract class AbstractChildQuery<T extends Element, Query extends SmofQuery<T, ?>> extends AbstractQuery<T, Query> {

	private final QueryOperators op;
	private final AbstractQuery<T, SmofQuery<T, ?>> parent;
	
	AbstractChildQuery(AbstractQuery<T, SmofQuery<T, ?>> parent, QueryOperators op) {
		super(parent.getParser(), parent.getElementClass());
		this.parent = parent;
		this.op = op;
	}
	
	public SmofQuery<T, SmofQuery<T, ?>> end() {
		parent.append(op.getFieldName(), getFilter());
		return parent;
	}

	@Override
	public SmofResults<T> results() {
		return end().results();
	}
}
