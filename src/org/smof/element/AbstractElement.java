package org.smof.element;

import java.lang.reflect.Field;
import java.util.Map;

import org.bson.types.ObjectId;
import org.smof.annnotations.SmofObjectId;
import org.smof.exception.SmofException;
import org.smof.field.PrimaryField;

@SuppressWarnings("javadoc")
public abstract class AbstractElement implements Element {

	private void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	@SmofObjectId(name = Element.ID, ref = "")
	private ObjectId id;
	
	private LazyLoadContext context;

	protected AbstractElement() {
		this(new ObjectId());
	}

	protected AbstractElement(final ObjectId initialID) {
		id = initialID;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public void setId(final ObjectId id) {
		this.id = id;
	}

	@Override
	public String getIdAsString() {
		return id.toHexString();
	}

	@Override
	public void lazyLoad() {
		final Map<PrimaryField, ObjectId> refs = context.getReferences();
		for(PrimaryField field : refs.keySet()) {
			final Object parsedObject = context.fromObjectId(id, field.getFieldClass());
			setField(field, parsedObject);
		}
	}

	private void setField(PrimaryField field, Object parsedObject) {
		final Field rawField = field.getRawField();
		try {
			rawField.setAccessible(true);
			rawField.set(this, parsedObject);
			rawField.setAccessible(false);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			handleError(e);
		}
	}

	@Override
	public void setLazyLoadContext(LazyLoadContext context) {
		this.context = context;
	}
}
