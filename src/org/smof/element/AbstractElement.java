package org.smof.element;

import org.bson.types.ObjectId;

@SuppressWarnings("javadoc")
public abstract class AbstractElement implements Element {

	private ObjectId id;

	protected AbstractElement() {
		this.id = null;
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
		Element.validateStringId(String.class.cast(id));

		this.id = id;
	}

	@Override
	public String getIdAsString() {
		return id.toHexString();
	}
}
