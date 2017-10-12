package org.smof.parsers.metadata;

import java.util.Set;

import org.smof.element.Element;
import org.smof.index.InternalIndex;
import org.smof.parsers.SmofParserPool;

@SuppressWarnings("javadoc")
public interface SmofTypeContext {
	
	public static SmofTypeContext create() {
		return new SmofTypeContextImpl();
	}

	public <T> TypeStructure<T> getTypeStructure(Class<T> type, SmofParserPool parsers);
	
	public void put(Class<?> type, SmofParserPool parsers);

	public <T> void putWithFactory(Class<T> type, Object factory, SmofParserPool parsers);

	public <T extends Element> Set<InternalIndex> getIndexes(Class<T> elClass);

}
