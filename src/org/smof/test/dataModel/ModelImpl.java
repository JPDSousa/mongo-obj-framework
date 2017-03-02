package org.smof.test.dataModel;

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
	
	@SmofNumber(name = UNITS)
	private int units;
	
	@SmofNumber(name = PRICE)
	private final int price;
	
	@SmofObject(name = BRAND)
	private final Brand brand;
	
	@SmofNumber(name = POPULARITY)
	private float popularity;
	
	@SmofArray(name = COLORS, type = SmofType.ARRAY)
	private final List<String> colors;
	
	ModelImpl(String name, int price, Brand brand, List<String> colors) {
		super();
		this.units = 0;
		this.price = price;
		this.brand = brand;
		this.popularity = 0;
		this.colors = colors;
		this.name = name;
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
		int result = 1;
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((colors == null) ? 0 : colors.hashCode());
		result = prime * result + Float.floatToIntBits(popularity);
		result = prime * result + price;
		result = prime * result + units;
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
		ModelImpl other = (ModelImpl) obj;
		if (brand == null) {
			if (other.brand != null) {
				return false;
			}
		} else if (!brand.equals(other.brand)) {
			return false;
		}
		if (colors == null) {
			if (other.colors != null) {
				return false;
			}
		} else if (!colors.equals(other.colors)) {
			return false;
		}
		if (Float.floatToIntBits(popularity) != Float.floatToIntBits(other.popularity)) {
			return false;
		}
		if (price != other.price) {
			return false;
		}
		if (units != other.units) {
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

}
