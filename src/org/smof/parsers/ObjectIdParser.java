package org.smof.parsers;

import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.smof.annnotations.SmofField;

class ObjectIdParser extends AbstractBsonParser {
	
	ObjectIdParser(SmofParser parser) {
		super(parser, ObjectId.class);
	}

	@Override
	public BsonValue toBson(Object value, SmofField fieldOpts) {
		return new BsonObjectId((ObjectId) value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		return (T) value.asObjectId().getValue();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return value.isObjectId();
	}

}
