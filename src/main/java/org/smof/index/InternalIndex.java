/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.smof.annnotations.SmofFilter;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexField;
import org.smof.annnotations.SmofPFEQuery;
import org.smof.annnotations.SmofQueryA;
import org.smof.element.Element;
import org.smof.exception.SmofException;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

@SuppressWarnings("javadoc")
public class InternalIndex {
	
	private static final String UNIQUE = "unique";

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	public static InternalIndex fromSmofIndex(SmofIndex note) {
		return new InternalIndex(createRawIndexes(note.fields()), createOptions(note));
	}
	
	private static Set<Bson> createRawIndexes(SmofIndexField[] fields) {
		final Set<Bson> indexes = new LinkedHashSet<>();
		if(fields.length == 1) {
			indexes.add(fromSmofIndexField(fields[0]));
		}
		else if(fields.length > 1) {
			indexes.addAll(fromSmofIndexFields(fields));
		}
		return indexes;
	}
	
	private static Bson fromSmofIndexField(SmofIndexField field) {
		final String fieldName = field.name();
		switch(field.type()) {
		case ASCENDING:
			return Indexes.ascending(fieldName);
		case DESCENDING:
			return Indexes.descending(fieldName);
		case TEXT:
			return Indexes.text(fieldName);
		}
		handleError(new IllegalArgumentException("Invalid index field."));
		return null;
	}
	
	private static List<Bson> fromSmofIndexFields(SmofIndexField[] fields) {
		return Arrays.stream(fields)
				.map(InternalIndex::fromSmofIndexField)
				.collect(Collectors.toList());
	}
	
	private static IndexOptions createOptions(SmofIndex note) {
		final IndexOptions options = new IndexOptions();
		options.unique(note.unique());
		SmofPFEQuery pfe=note.pfe();
		if(!pfe.name().equals("default")){
			BsonDocument bson=new BsonDocument();
			SmofQueryA[] query=pfe.expression();
			BsonDocument bson1=new BsonDocument();
			SmofFilter[] filter=query[0].query();
			bson1.append(filter[0].operator().getMongoToken(),new BsonString(filter[0].value()));
			bson.append(query[0].name(),bson1);
			options.partialFilterExpression(bson);
		}
		return options;
	}

	public static InternalIndex fromBson(BsonDocument doc) {
		return new InternalIndex(createRawIndexes(doc), createOptions(doc));
	}
	
	private static Set<Bson> createRawIndexes(BsonDocument doc) {
		final Set<Bson> indexes = new LinkedHashSet<>();
		final String name = doc.getString("name").getValue();
		if(name.equals("_id_")) {
			indexes.add(Indexes.ascending(Element.ID));
		}
		else {
			indexes.addAll(parseIndexName(name));
		}
		return indexes;
	}

	private static List<Bson> parseIndexName(String name) {
		final StringTokenizer tokens = new StringTokenizer(name, "_");
		final List<Bson> indexes = new ArrayList<>();
		
		while(tokens.hasMoreTokens()) {
			Bson index = nextIndex(tokens);
			indexes.add(index);
		}
		return indexes;
	}

	private static Bson nextIndex(StringTokenizer tokens) {
		final Bson index;
		final StringBuilder indexName = new StringBuilder(tokens.nextToken());
		String indexTypeStr = tokens.nextToken();
		IndexType indexT;
		
		while((indexT = IndexType.parse(indexTypeStr)) == null) {
			indexName.append("_").append(indexTypeStr);
			indexTypeStr = tokens.nextToken();
		}
		
		switch(indexT) {
		case ASCENDING:
			index = Indexes.ascending(indexName.toString());
			break;
		case DESCENDING:
			index = Indexes.descending(indexName.toString());
			break;
		case TEXT:
			index = Indexes.text(indexName.toString());
			break;
		default:
			handleError(new IllegalArgumentException("Invalid bson index"));
			index = null;
			break;
		}
		return index;
	}
	
	private static IndexOptions createOptions(BsonDocument doc) {
		final IndexOptions options = new IndexOptions();
		options.unique(isUnique(doc));
		return options;
	}

	private static boolean isUnique(BsonDocument doc) {
		return doc.containsKey(UNIQUE) && doc.getBoolean(UNIQUE).getValue();
	}
	
	private final Bson index;
	private final IndexOptions options;
	private final Set<Bson> rawIndexes;
	
	private InternalIndex(Set<Bson> indexes, IndexOptions options) {
		rawIndexes = indexes;
		index = fromIndexes(new ArrayList<>(indexes));
		this.options = options;
	}

	private Bson fromIndexes(List<Bson> indexes) {
		if(indexes.size() == 1) {
			return indexes.get(0);
		}
		return Indexes.compoundIndex(indexes);
	}

	public Bson getIndex() {
		return index;
	}

	public IndexOptions getOptions() {
		return options;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rawIndexes == null) ? 0 : rawIndexes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InternalIndex other = (InternalIndex) obj;
		if (rawIndexes == null) {
			if (other.rawIndexes != null) {
				return false;
			}
		} else if (!rawIndexes.equals(other.rawIndexes)) {
			return false;
		}
		return true;
	}
	
	
}
