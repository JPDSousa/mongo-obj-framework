package org.smof.dataModel;

import java.time.LocalDate;

import org.smof.annnotations.SmofDate;
import org.smof.annnotations.SmofString;
import org.smof.element.AbstractElement;

class OwnerImpl extends AbstractElement implements Owner {

	@SmofString(name = NAME)
	private final String name;
	
	@SmofDate(name = BIRTHDAY)
	private final LocalDate birthday;
	
	OwnerImpl(String name, LocalDate birthday) {
		super();
		this.name = name;
		this.birthday = birthday;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public LocalDate getBirthday() {
		return birthday;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OwnerImpl other = (OwnerImpl) obj;
		if (birthday == null) {
			if (other.birthday != null) {
				return false;
			}
		} else if (!birthday.equals(other.birthday)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "OwnerImpl [name=" + name + ", birthday=" + birthday + "]";
	}
	
	

}
