package org.smof.test.dataModel;

import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofString;
import org.smof.element.AbstractElement;

@SuppressWarnings("javadoc")
public abstract class AbstractGuitar extends AbstractElement implements Guitar{
	
	@SmofObject(name=MODEL)
	private final Model model;
	
	@SmofString(name=TYPE)
	private final TypeGuitar type;
	
	@SmofString(name=OWNER)
	private String owner;
	
	@SmofString(name=COLOR)
	private String color;
	
	@SmofNumber(name=PRICE)
	private int price;
	
	protected AbstractGuitar(Model model, TypeGuitar type) {
		super();
		this.model = model;
		this.type = type;
		price = model.getFactoryPrice();
	}

	@Override
	public Brand getBrand() {
		return model.getBrand();
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public TypeGuitar getGuitarType() {
		return type;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public String getColor() {
		return color;
	}
	
	@Override
	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public int getPrice() {
		return price;
	}

	@Override
	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + price;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AbstractGuitar other = (AbstractGuitar) obj;
		if (color == null) {
			if (other.color != null) {
				return false;
			}
		} else if (!color.equals(other.color)) {
			return false;
		}
		if (model == null) {
			if (other.model != null) {
				return false;
			}
		} else if (!model.equals(other.model)) {
			return false;
		}
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		if (price != other.price) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
