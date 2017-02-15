package org.smof.query;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.BsonDocument;
import org.smof.element.Element;
import org.smof.element.SmofAdapter;

import com.mongodb.client.FindIterable;

@SuppressWarnings("javadoc")
public class SmofResults<T extends Element> {
	
	private final FindIterable<BsonDocument> rawResults;
	private final SmofAdapter<T> parser;

	public SmofResults(FindIterable<BsonDocument> rawResults, SmofAdapter<T> parser) {
		this.rawResults = rawResults;
		this.parser = parser;
	}
	
	public Stream<T> stream() {
		return StreamSupport.stream(rawResults.spliterator(), false)
				.map(d -> parser.read(d));
	}
	
	public List<T> asList() {
		return stream().collect(Collectors.toList());
	}
	
	
}
