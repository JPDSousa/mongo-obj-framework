package org.smof.index;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexField;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

@SuppressWarnings("javadoc")
public class InternalIndex {
	
	private final Bson index;
	
	private final IndexOptions options;
	
	public InternalIndex(SmofIndex note) {
		options = new IndexOptions();
		options.unique(note.unique());
		index = createIndex(note.fields());
	}

	private Bson createIndex(SmofIndexField[] fields) {
		if(fields.length == 1) {
			return createIndex(fields[0]);
		}
		else if(fields.length > 1) {
			return Indexes.compoundIndex(toIndexes(fields));
		}
		return null;
	}

	private Bson createIndex(SmofIndexField field) {
		final String fieldName = field.name();
		switch(field.type()) {
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

	private List<Bson> toIndexes(SmofIndexField[] fields) {
		return Arrays.stream(fields)
				.map(f -> createIndex(f))
				.collect(Collectors.toList());
	}

	public Bson getIndex() {
		return index;
	}

	public IndexOptions getOptions() {
		return options;
	}

	

}
