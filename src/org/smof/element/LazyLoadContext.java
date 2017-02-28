package org.smof.element;

import java.util.Map;

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
	
	LazyLoadContext(SmofDispatcher dispatcher, Map<PrimaryField, ObjectId> references) { 
		super();
		this.dispatcher = dispatcher;
		this.references = references;
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
