package org.smof.query;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.smof.element.Element;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;

@SuppressWarnings("javadoc")
public class SmofResults<T extends Element> {
	
	private final FindIterable<Document> rawResults;
	private final Gson jsonManager;
	private final Class<T> type;

	public SmofResults(FindIterable<Document> rawResults, Gson jsonManager, Class<T> type) {
		this.rawResults = rawResults;
		this.jsonManager = jsonManager;
		this.type = type;
	}
	
	public Stream<T> stream() {
		return StreamSupport.stream(rawResults.spliterator(), false)
				.map(d -> jsonManager.fromJson(d.toJson(), type));
	}
	
	public List<T> asList() {
		return stream().collect(Collectors.toList());
	}
	
	
}
