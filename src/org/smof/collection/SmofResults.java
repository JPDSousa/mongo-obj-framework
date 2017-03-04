package org.smof.collection;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.parsers.SmofParser;

import com.google.common.cache.Cache;
import com.mongodb.client.FindIterable;

@SuppressWarnings("javadoc")
public class SmofResults<T extends Element> {
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private final FindIterable<BsonDocument> rawResults;
	private final SmofParser parser;
	private final Class<T> elClass;
	private final Cache<ObjectId, T> cache;

	SmofResults(FindIterable<BsonDocument> rawResults, SmofParser parser, Class<T> elementClass, Cache<ObjectId, T> cache) {
		this.rawResults = rawResults;
		this.parser = parser;
		elClass = elementClass;
		this.cache = cache;
	}
	
	public Stream<T> stream() {
		return StreamSupport.stream(rawResults.spliterator(), false)
				.map(d -> parse(d))
				.filter(d -> d != null);
	}

	private T parse(BsonDocument d) {
		try {
			if(d != null) {
				final ObjectId id = d.get(Element.ID).asObjectId().getValue();
				return cache.get(id, new DocumentParser(d));	
			}
		} catch (ExecutionException e) {
			handleError(e);
		}
		return null;
	}
	
	public List<T> asList() {
		return stream().collect(Collectors.toList());
	}

	public T first() {
		final BsonDocument result = rawResults.first();
		return parse(result);
	}
	
	private class DocumentParser implements Callable<T>{

		private final BsonDocument document;
		
		private DocumentParser(BsonDocument document) {
			super();
			this.document = document;
		}

		@Override
		public T call() throws Exception {
			return parser.fromBson(document, elClass);
		}
	}
	
}
