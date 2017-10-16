package org.smof.parsers.metadata;

import java.util.Set;

import org.smof.element.Element;
import org.smof.index.InternalIndex;
import org.smof.parsers.SmofParserPool;

@SuppressWarnings("javadoc")
public interface SmofTypeContext {
	
	static SmofTypeContext create() {
		return new SmofTypeContextImpl();
	}

	<T> TypeStructure<T> getTypeStructure(Class<T> type, SmofParserPool parsers);
	
	void put(Class<?> type, SmofParserPool parsers);

	<T> void putWithFactory(Class<T> type, Object factory, SmofParserPool parsers);

	<T extends Element> Set<InternalIndex> getIndexes(Class<T> elClass);

}
