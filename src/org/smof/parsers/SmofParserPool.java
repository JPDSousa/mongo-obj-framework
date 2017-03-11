package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smof.collection.SmofDispatcher;

import static org.smof.parsers.SmofType.*;

@SuppressWarnings("javadoc")
public class SmofParserPool {
	
	public static SmofParserPool create(SmofParser parserContext, SmofDispatcher dispatcher) {
		return new SmofParserPool(parserContext, dispatcher);
	}
	
	private final Map<SmofType, BsonParser> parsers;
	
	private SmofParserPool(SmofParser parserContext, SmofDispatcher dispatcher) {
		parsers = new LinkedHashMap<>();
		createParsers(parserContext, dispatcher);
	}

	private void createParsers(SmofParser parserContext, SmofDispatcher dispatcher) {
		parsers.put(ARRAY, new ArrayParser(parserContext, dispatcher));
		parsers.put(DATETIME, new DateTimeParser(parserContext, dispatcher));
		parsers.put(NUMBER, new NumberParser(parserContext, dispatcher));
		parsers.put(OBJECT, new ObjectParser(parserContext, dispatcher));
		parsers.put(OBJECT_ID, new ObjectIdParser(parserContext, dispatcher));
		parsers.put(STRING, new StringParser(parserContext, dispatcher));
		parsers.put(BYTE, new ByteParser(dispatcher, parserContext));
	}
	
	public BsonParser get(SmofType type) {
		return parsers.get(type);
	}
}
