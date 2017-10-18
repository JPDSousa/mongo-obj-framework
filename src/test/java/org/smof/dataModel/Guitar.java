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

import java.nio.file.Path;

import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexField;
import org.smof.annnotations.SmofIndexes;
import org.smof.annnotations.SmofParam;
import org.smof.element.Element;
import org.smof.gridfs.SmofGridRef;
import org.smof.index.IndexType;

@SuppressWarnings("javadoc")
@SmofIndexes({
	@SmofIndex(fields = {
			@SmofIndexField(name = "owner", type = IndexType.TEXT)
	}),
	@SmofIndex(fields = {
			@SmofIndexField(name = "price", type = IndexType.ASCENDING) 
	}),
	@SmofIndex(fields = {
			@SmofIndexField(name = "model", type = IndexType.ASCENDING),
			@SmofIndexField(name = "type", type = IndexType.ASCENDING),
			@SmofIndexField(name = "age", type = IndexType.ASCENDING)
	}, unique = true)
})
public interface Guitar extends Element{

	String BRAND = "brand";
	String MODEL = "model";
	String TYPE = "type";
	String NECKS = "necks";
	String AGE = "age";
	String PRICE = "price";
	String COLOR = "color";
	String OWNER = "owner";
	String PICTURE = "pic";

	@SmofBuilder
	static Guitar create(
			@SmofParam(name=MODEL) Model model, 
			@SmofParam(name=TYPE) TypeGuitar type, 
			@SmofParam(name=NECKS) Integer neckNumber, 
			@SmofParam(name=AGE) Integer age) {
		switch(type) {
		case ACOUSTIC:
			return new AcousticGuitar(model);
		case CLASSIC:
			return new ClassicalGuitar(model, age);
		case ELECTRIC:
			return new ElectricGuitar(model, neckNumber);
		default:
			return null;
		}
	}

	Brand getBrand();

	Model getModel();

	TypeGuitar getGuitarType();

	String getOwner();

	String getColor();

	void setColor(String color);

	void setPrice(int price);

	int getPrice();
	
	SmofGridRef getPicture();
	
	void setPicture(Path picture);

}
