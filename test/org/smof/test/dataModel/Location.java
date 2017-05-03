package org.smof.test.dataModel;

import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;

@SuppressWarnings("javadoc")
public class Location {

	private static final String COUNTRY = "country";

	private static final String CITY = "city";

	@SmofString(name = CITY)
	private final String city;
	
	@SmofString(name = COUNTRY)
	private final String country;
	
	@SmofBuilder
	public Location(
			@SmofParam(name = CITY) String city, 
			@SmofParam(name = COUNTRY) String country) {
		super();
		this.city = city;
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
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
		Location other = (Location) obj;
		if (city == null) {
			if (other.city != null) {
				return false;
			}
		} else if (!city.equals(other.city)) {
			return false;
		}
		if (country == null) {
			if (other.country != null) {
				return false;
			}
		} else if (!country.equals(other.country)) {
			return false;
		}
		return true;
	}
	
}
