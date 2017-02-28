package org.smof.collection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.mongodb.client.FindIterable;

@SuppressWarnings("javadoc")
public class SmofResults<T extends Element> {
	
	private final FindIterable<BsonDocument> rawResults;
	private final SmofParser parser;
	private final Class<T> elClass;
	private final ElementCache<T> cache;

	SmofResults(FindIterable<BsonDocument> rawResults, SmofParser parser, Class<T> elementClass, ElementCache<T> cache) {
		this.rawResults = rawResults;
		this.parser = parser;
		elClass = elementClass;
		this.cache = cache;
	}
	
	public Stream<T> stream() {
		return StreamSupport.stream(rawResults.spliterator(), false)
				.map(d -> parse(d));
	}

	private T parse(BsonDocument d) {
		final ObjectId id = d.get(Element.ID).asObjectId().getValue();
		if(cache.contains(id)) {
			return cache.get(id);
		}
		return parser.fromBson(d, elClass);
	}
	
	public List<T> asList() {
		return stream().collect(Collectors.toList());
	}

	public T first() {
		final BsonDocument result = rawResults.first();
		return parse(result);
	}
	
	
}
