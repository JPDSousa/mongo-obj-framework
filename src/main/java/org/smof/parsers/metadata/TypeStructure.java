package org.smof.parsers.metadata;

import java.util.Map;
import java.util.Set;

import org.smof.field.PrimaryField;
import org.smof.index.InternalIndex;

@SuppressWarnings("javadoc")
public interface TypeStructure<T> {
	
	static <T> TypeStructure<T> create(Class<T> type, TypeBuilder<T> builder) {
		return new TypeStructureImpl<>(type, builder);
	}

	Map<String, PrimaryField> getAllFields();

	<E> TypeParser<E> getParser(Class<E> type);

	TypeBuilder<T> getBuilder();

	<E> void addSubType(Class<E> subType, TypeParser<E> parser);

	boolean containsSub(Class<?> type);

	Set<InternalIndex> getIndexes();

}
