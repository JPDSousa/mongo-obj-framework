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

import org.smof.annnotations.OperatorType;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofFilter;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexField;
import org.smof.annnotations.SmofIndexes;
import org.smof.annnotations.SmofPFEQuery;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofQueryA;
import org.smof.element.Element;
import org.smof.index.IndexType;

@SuppressWarnings("javadoc")
@SmofIndexes({
	@SmofIndex(fields = {@SmofIndexField(name = "units", type = IndexType.ASCENDING), @SmofIndexField(name = "popularity", type = IndexType.DESCENDING)}),
	@SmofIndex(fields = {@SmofIndexField(name = "name", type = IndexType.TEXT), @SmofIndexField(name = "creator", type = IndexType.TEXT)},
			   pfe=@SmofPFEQuery(expression=@SmofQueryA(name = "name",query={@SmofFilter(operator = OperatorType.exists, value = "true")})
		))
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
	
	@Override
	boolean equals(Object model);
	
	@Override
	int hashCode();
}
