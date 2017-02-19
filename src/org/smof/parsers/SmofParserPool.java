package org.smof.parsers;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.smof.parsers.SmofType.*;

@SuppressWarnings("javadoc")
public class SmofParserPool {
	
	public static SmofParserPool create(SmofParser parserContext) {
		return new SmofParserPool(parserContext);
	}
	
	private final Map<SmofType, BsonParser> parsers;
	
	private SmofParserPool(SmofParser parserContext) {
		parsers = new LinkedHashMap<>();
		createParsers(parserContext);
	}

	private void createParsers(SmofParser parserContext) {
		parsers.put(ARRAY, new ArrayParser(parserContext));
		parsers.put(DATETIME, new DateTimeParser(parserContext));
		parsers.put(NUMBER, new NumberParser(parserContext));
		parsers.put(OBJECT, new ObjectParser(parserContext));
		parsers.put(OBJECT_ID, new ObjectIdParser(parserContext));
		parsers.put(STRING, new StringParser(parserContext));
	}
	
	public BsonParser get(SmofType type) {
		return parsers.get(type);
	}
}
