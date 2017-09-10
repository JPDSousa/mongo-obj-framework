package org.smof.utils;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.smof.element.Element;
import org.smof.parsers.BsonLazyObjectId;
import org.smof.parsers.SmofParser;

@SuppressWarnings("javadoc")
public class BsonUtils {
	
	public final static BsonObjectId toBsonObjectId(Element value) {
		final ObjectId id = value.getId();
		return new BsonObjectId(id);
	}
	
	public final static Stack<Pair<String, Element>> extrackPosInsertions(BsonDocument doc) {
		final BsonArray bsonPosInsert = doc.remove(SmofParser.ON_INSERT).asArray();
		final Stack<Pair<String, Element>> posInsertions = new Stack<>();
		bsonPosInsert.stream()
			.map(v -> (BsonLazyObjectId) v)
			.map(id -> Pair.of(id.getFieldName(), id.getElement()))
			.forEach(posInsertions::push);
		return posInsertions;
	}

}
