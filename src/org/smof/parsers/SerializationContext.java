package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonValue;

class SerializationContext {
	
	public static SerializationContext create() {
		return new SerializationContext();
	}

	private final Map<Pair<Object, SmofType>, BsonValue> serializationStack;
	
	private SerializationContext() {
		serializationStack = new LinkedHashMap<>();
	}
	
	void put(Object object, SmofType type, BsonValue serializedValue) {
		serializationStack.put(Pair.of(object, type), serializedValue);
	}
	
	boolean contains(Object object, SmofType type) {
		return serializationStack.containsKey(Pair.of(object, type));
	}
	
	BsonValue get(Object object, SmofType type) {
		return serializationStack.get(Pair.of(object, type));
	}
	
	void clear() {
		serializationStack.clear();
	}
}
