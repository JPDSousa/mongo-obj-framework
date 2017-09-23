package org.smof.dataModel;

import org.smof.annnotations.SmofNumber;

@SuppressWarnings("javadoc")
public class ElectricGuitar extends AbstractGuitar {

	@SmofNumber(name=NECKS)
	private final int neckNumber;
	
	ElectricGuitar(Model model, int neckNumber) {
		super(model, TypeGuitar.ELECTRIC);
		this.neckNumber = neckNumber;
	}

	public int getNeckNumber() {
		return neckNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + neckNumber;
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
		ElectricGuitar other = (ElectricGuitar) obj;
		if (neckNumber != other.neckNumber) {
			return false;
		}
		return true;
	}
}
