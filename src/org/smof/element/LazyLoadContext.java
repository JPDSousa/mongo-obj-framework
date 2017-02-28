package org.smof.element;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.smof.collection.SmofDispatcher;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;

class LazyLoadContext {
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private final SmofDispatcher dispatcher;
	private final Map<PrimaryField, ObjectId> references;
	
	LazyLoadContext(SmofDispatcher dispatcher, BsonDocument references, Set<PrimaryField> externalFields) { 
		super();
		this.dispatcher = dispatcher;
		this.references = buildReferences(references, externalFields);
	}

	private Map<PrimaryField, ObjectId> buildReferences(BsonDocument references, Set<PrimaryField> externalFields) {
		final Map<PrimaryField, ObjectId> refs = new LinkedHashMap<>();
		for(PrimaryField primary : externalFields) {
			final ObjectId id = references.getObjectId(primary.getName()).getValue();
			refs.put(primary, id);
		}
		return refs;
	}

	@SuppressWarnings("unchecked")
	Object fromObjectId(ObjectId id, Class<?> elementType) {
		checkValidElementClass(elementType);
		return dispatcher.findById(id, (Class<Element>) elementType);
	}
	
	private void checkValidElementClass(Class<?> elementClass) {
		if(!Element.class.isAssignableFrom(elementClass)) {
			handleError(new IllegalArgumentException(elementClass.getName() + " does not implement Element class."));
		}
	}

	Map<PrimaryField,ObjectId> getReferences() {
		return references;
	}
}
