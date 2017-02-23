package org.smof.index;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;
import org.smof.annnotations.PrimaryField;
import org.smof.annnotations.SmofIndex;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

@SuppressWarnings("javadoc")
public class InternalIndex {
	
	private final String key;
	
	private final Bson index;
	
	private final IndexOptions options;
	
	public InternalIndex(SmofIndex note, List<PrimaryField> fields) {
		key = note.key();
		options = new IndexOptions();
		options.unique(note.unique());
		index = createIndex(fields);
	}

	private Bson createIndex(List<PrimaryField> fields) {
		if(fields.size() == 1) {
			return fields.get(0).getIndex();
		}
		else if(fields.size() > 1) {
			return Indexes.compoundIndex(toIndexes(fields));
		}
		return null;
	}

	private List<Bson> toIndexes(List<PrimaryField> fields) {
		return fields.stream()
				.map(f -> f.getIndex())
				.collect(Collectors.toList());
	}

	public String getKey() {
		return key;
	}

	public Bson getIndex() {
		return index;
	}

	public IndexOptions getOptions() {
		return options;
	}

	

}
