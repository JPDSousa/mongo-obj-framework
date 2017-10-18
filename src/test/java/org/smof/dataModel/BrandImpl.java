/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
	public void multiplyCapital(long value) {
		this.capital *= value;
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
