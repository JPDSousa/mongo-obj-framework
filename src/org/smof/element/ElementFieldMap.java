package org.smof.element;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("javadoc")
public class ElementFieldMap {
	
	private final Map<String, Object> fieldMap;
	
	ElementFieldMap() {
		this(new LinkedHashMap<>());
	}
	
	ElementFieldMap(Map<String, Object> fieldMap) {
		this.fieldMap = fieldMap;
	}
	
	void put(String key, Object value) {
		fieldMap.put(key, value);
	}

	public <T> T get(String fieldName, Class<T> fieldType) {
		return  fieldType.cast(fieldMap.get(fieldName));
	}
}
