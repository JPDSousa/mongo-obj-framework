package org.smof.element;

@SuppressWarnings("javadoc")
public abstract class AbstractElement implements Element {

	private String id;

	protected AbstractElement() {
		this.id = null;
	}

	protected AbstractElement(final String initialID) {
		id = initialID;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void setID(final String id) throws InvalidIdException {
		Element.validateStringID(String.class.cast(id));

		this.id = id;
	}
}
