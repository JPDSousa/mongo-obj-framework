package org.smof.element;

import org.bson.types.ObjectId;
import org.smof.annnotations.SmofObjectId;
import org.smof.exception.InvalidIdException;

@SuppressWarnings("javadoc")
public abstract class AbstractElement implements Element {

	@SmofObjectId(name = Element.ID, ref = "")
	private ObjectId id;

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
	public void setId(final ObjectId id) throws InvalidIdException {
		this.id = id;
	}

	@Override
	public String getIdAsString() {
		return id.toHexString();
	}
}
