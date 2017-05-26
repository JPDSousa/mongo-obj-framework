package org.smof.examples.dataModel;

import java.util.List;

import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexField;
import org.smof.annnotations.SmofIndexes;
import org.smof.annnotations.SmofParam;
import org.smof.element.Element;
import org.smof.index.IndexType;

@SuppressWarnings("javadoc")
@SmofIndexes({
	@SmofIndex(fields = {@SmofIndexField(name = "units", type = IndexType.ASCENDING), @SmofIndexField(name = "popularity", type = IndexType.DESCENDING)}),
	@SmofIndex(fields = {@SmofIndexField(name = "name", type = IndexType.TEXT), @SmofIndexField(name = "creator", type = IndexType.TEXT)})
	})
public interface Model extends Element {

	String UNITS = "units";
	String POPULARITY = "popularity";
	String BRAND = "brand";
	String PRICE = "price";
	String COLORS = "colors";
	String NAME = "name";
	String CREATOR = "creator";
	
	@SmofBuilder
	static Model create(
			@SmofParam(name = NAME) String name,
			@SmofParam(name = CREATOR) String creator,
			@SmofParam(name = PRICE) Integer price, 
			@SmofParam(name = BRAND) Brand brand, 
			@SmofParam(name = COLORS) List<String> colors) {
		return new ModelImpl(name, creator, price, brand, colors);
	}

	int getUnits();
	void addUnits(int units);
	
	int getFactoryPrice();
	
	String getName();
	
	String getCreator();
	
	Brand getBrand();
	
	List<String> getAvailableColors();
	
	float getPopularity();
	void setPopularity(float popularity);
}
