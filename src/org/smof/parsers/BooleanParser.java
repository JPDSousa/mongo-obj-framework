package org.smof.parsers;

import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.smof.collection.SmofDispatcher;
import org.smof.field.SmofField;

@SuppressWarnings("javadoc")
public class BooleanParser extends AbstractBsonParser {

	private static final Class<?>[] VALID_TYPES = {boolean.class, Boolean.class};

	protected BooleanParser(SmofParser bsonParser, SmofDispatcher dispatcher) {
		super(dispatcher, bsonParser, VALID_TYPES);
	}

	@Override
	protected BsonValue serializeToBson(Object value, SmofField fieldOpts) {
		if(isBoolean(value.getClass())) {
			return fromBoolean((Boolean) value);
		}
		return null;
	}

	private BsonValue fromBoolean(Boolean value) {
		return new BsonBoolean(value);
	}

	private boolean isBoolean(Class<?> type) {
		return type.equals(boolean.class) || type.equals(Boolean.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		return (T) new Boolean(value.asBoolean().getValue());
	}

}
