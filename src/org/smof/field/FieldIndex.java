package org.smof.field;

import org.bson.conversions.Bson;
import org.smof.index.IndexType;

import com.mongodb.client.model.Indexes;

class FieldIndex {

	static FieldIndex create(String key, IndexType type) {
		if(key != "") {
			return new FieldIndex(key, type);
		}
		return null;
	}
	
	private final String key;
	private final IndexType type;
	
	private FieldIndex(String key, IndexType type) {
		super();
		this.key = key;
		this.type = type;
	}

	String getKey() {
		return key;
	}

	IndexType getType() {
		return type;
	}
	
	Bson toBson(String fieldName) {
		switch(type) {
		case ASCENDING:
			return Indexes.ascending(fieldName);
		case DESCENDING:
			return Indexes.descending(fieldName);
		case HASHED:
			return Indexes.hashed(fieldName);
		case TEXT:
			return Indexes.text(fieldName);
		}
		return null;
	}
}
