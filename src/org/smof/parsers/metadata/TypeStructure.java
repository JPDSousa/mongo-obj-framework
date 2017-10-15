package org.smof.parsers.metadata;

import java.util.Map;
import java.util.Set;

import org.smof.field.PrimaryField;
import org.smof.index.InternalIndex;

@SuppressWarnings("javadoc")
public interface TypeStructure<T> {
	
	public static <T> TypeStructure<T> create(Class<T> type, TypeBuilder<T> builder) {
		return new TypeStructureImpl<>(type, builder);
	}

	public Map<String, PrimaryField> getAllFields();

	public <E> TypeParser<E> getParser(Class<E> type);

	public TypeBuilder<T> getBuilder();

	public <E> void addSubType(Class<E> subType, TypeParser<E> parser);

	public boolean containsSub(Class<?> type);

	Set<InternalIndex> getIndexes();

}
