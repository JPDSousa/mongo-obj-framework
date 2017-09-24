package org.smof.dataModel;

import java.time.LocalDate;
import java.util.List;

import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofDate;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofString;
import org.smof.element.AbstractElement;
import org.smof.parsers.SmofType;

class BrandImpl extends AbstractElement implements Brand {
	
	@SmofString(name = NAME)
	private final String name;
	
	@SmofArray(name = OWNERS, type = SmofType.OBJECT)
	private final List<Owner> owners;
	
	@SmofDate(name = FOUNDING)
	private final LocalDate founding;
	
	@SmofObject(name = LOCATION)
	private final Location headQuarters;
	
	@SmofNumber(name = CAPITAL)
	private long capital;

	BrandImpl(String name, Location headQuarters, List<Owner> owners) {
		super();
		this.name = name;
		this.owners = owners;
		this.founding = LocalDate.now();
		this.capital = 0;
		this.headQuarters = headQuarters;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Owner> getOwners() {
		return owners;
	}

	@Override
	public LocalDate getFoundingDate() {
		return founding;
	}

	@Override
	public long getCapital() {
		return capital;
	}

	@Override
	public void setCapital(long value) {
		this.capital = value;
	}

	@Override
	public void increaseCapital(long value) {
		this.capital += value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (capital ^ (capital >>> 32));
		result = prime * result + ((founding == null) ? 0 : founding.hashCode());
		result = prime * result + ((headQuarters == null) ? 0 : headQuarters.hashCode());
		result = prime * result + ((owners == null) ? 0 : owners.hashCode());
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
		BrandImpl other = (BrandImpl) obj;
		if (capital != other.capital) {
			return false;
		}
		if (founding == null) {
			if (other.founding != null) {
				return false;
			}
		} else if (!founding.equals(other.founding)) {
			return false;
		}
		if (headQuarters == null) {
			if (other.headQuarters != null) {
				return false;
			}
		} else if (!headQuarters.equals(other.headQuarters)) {
			return false;
		}
		if (owners == null) {
			if (other.owners != null) {
				return false;
			}
		} else if (!owners.equals(other.owners)) {
			return false;
		}
		return true;
	}

	@Override
	public Location getLocation() {
		return headQuarters;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
