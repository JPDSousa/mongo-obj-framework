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

import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexField;
import org.smof.annnotations.SmofIndexes;
import org.smof.annnotations.SmofParam;
import org.smof.element.Element;
import org.smof.index.IndexType;

@SuppressWarnings("javadoc")
@SmofIndexes({
	@SmofIndex(fields = {@SmofIndexField(name = "name", type = IndexType.TEXT)}, unique = true)
})
public interface Brand extends Element{

	String NAME = "name";
	String OWNERS = "owners";
	String FOUNDING = "founding";
	String CAPITAL = "capital";
	String LOCATION = "location";
	
	@SmofBuilder
	static Brand create(
			@SmofParam(name = NAME) String name,
			@SmofParam(name = LOCATION) Location headQuarters,
			@SmofParam(name = OWNERS) List<Owner> owners) {
		return new BrandImpl(name, headQuarters, owners);
	}
	
	String getName();
	
	List<Owner> getOwners();
	
	LocalDate getFoundingDate();
	
	double getCapital();
	
	void setCapital(double value);
	
	void increaseCapital(double value);
	
	Location getLocation();

	void multiplyCapital(double value);
}
