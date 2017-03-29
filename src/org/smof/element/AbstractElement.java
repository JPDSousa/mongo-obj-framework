package org.smof.element;

import org.bson.types.ObjectId;
import org.smof.annnotations.SmofObjectId;

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
	public void setId(final ObjectId id) {
		this.id = id;
	}

	@Override
	public String getIdAsString() {
		return id.toHexString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractElement other = (AbstractElement) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	
}
