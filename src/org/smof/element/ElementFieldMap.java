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

	public <T> T get(String fieldName, Class<T> fieldType) {
		return  fieldType.cast(fieldMap.get(fieldName));
	}
}
