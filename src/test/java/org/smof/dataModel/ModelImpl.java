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

import java.util.List;

import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofString;
import org.smof.element.AbstractElement;
import org.smof.parsers.SmofType;

class ModelImpl extends AbstractElement implements Model {

	@SmofString(name = NAME)
	private final String name;
	
	@SmofString(name = CREATOR)
	private final String creator;
	
	@SmofNumber(name = UNITS)
	private int units;
	
	@SmofNumber(name = PRICE)
	private final int price;
	
	@SmofObject(name = BRAND, preInsert = false)
	private final Brand brand;
	
	@SmofNumber(name = POPULARITY)
	private float popularity;
	
	@SmofArray(name = COLORS, type = SmofType.STRING)
	private final List<String> colors;
	
	ModelImpl(String name, String creator, int price, Brand brand, List<String> colors) {
		super();
		this.units = 0;
		this.price = price;
		this.brand = brand;
		this.popularity = 0;
		this.colors = colors;
		this.name = name;
		this.creator = creator;
	}

	@Override
	public int getUnits() {
		return units;
	}

	@Override
	public void addUnits(int units) {
		this.units += units;
	}

	@Override
	public int getFactoryPrice() {
		return price;
	}

	@Override
	public Brand getBrand() {
		return brand;
	}

	@Override
	public List<String> getAvailableColors() {
		return colors;
	}

	@Override
	public float getPopularity() {
		return popularity;
	}

	@Override
	public void setPopularity(float popularity) {
		this.popularity = popularity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
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
		ModelImpl other = (ModelImpl) obj;
		if (brand == null) {
			if (other.brand != null) {
				return false;
			}
		} else if (!brand.equals(other.brand)) {
			return false;
		}
		if (creator == null) {
			if (other.creator != null) {
				return false;
			}
		} else if (!creator.equals(other.creator)) {
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
	public String getName() {
		return name;
	}

	@Override
	public String getCreator() {
		return creator;
	}
	
	@Override
	public String toString() {
		return name + "-" + brand;
	}

}
