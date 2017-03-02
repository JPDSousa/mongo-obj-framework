package org.smof.test.dataModel;

import java.util.List;

import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofParam;
import org.smof.element.Element;

@SuppressWarnings("javadoc")
public interface Model extends Element {

	String UNITS = "units";
	String POPULARITY = "popularity";
	String BRAND = "brand";
	String PRICE = "price";
	String COLORS = "colors";
	String NAME = "name";
	
	@SmofBuilder
	static Model create(
			@SmofParam(name = NAME) String name,
			@SmofParam(name = PRICE) Integer price, 
			@SmofParam(name = BRAND) Brand brand, 
			@SmofParam(name = COLORS) List<String> colors) {
		return new ModelImpl(name, price, brand, colors);
	}

	int getUnits();
	void addUnits(int units);
	
	int getFactoryPrice();
	
	String getName();
	
	Brand getBrand();
	
	List<String> getAvailableColors();
	
	float getPopularity();
	void setPopularity(float popularity);
}
