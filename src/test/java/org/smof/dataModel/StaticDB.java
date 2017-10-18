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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("javadoc")
public final class StaticDB {

	public static final String MODELS = "models";
	public static final String BRANDS = "brands";
	public static final String OWNERS = "owners";
	public static final String GUITARS = "guitars";
	
	public static final String GUITARS_PIC_BUCKET = "guitarsPics";
	
	public static final Owner OWNER_1 = Owner.create("ziztheman", LocalDate.of(1990, 10, 12));
	
	public static final Brand BRAND_1 = Brand.create("Gibson", new Location("Nashville", "USA"), Collections.singletonList(OWNER_1));
	
	public static final Model MODEL_1 = Model.create("Manhattan", "Tyler", 1000, BRAND_1, Arrays.asList("red", "blue"));
	public static final Model MODEL_2 = Model.create("BeeGees", "Tyler", 5463, BRAND_1, Arrays.asList("sunburst", "ebony"));
	
	public static final Guitar GUITAR_3 = Guitar.create(MODEL_1, TypeGuitar.ACOUSTIC, 0, 1989);
	public static final Guitar GUITAR_2 = Guitar.create(MODEL_1, TypeGuitar.CLASSIC, 0, 1960);
	public static final Guitar GUITAR_1 = Guitar.create(MODEL_2, TypeGuitar.ELECTRIC, 1, 0);
	public static final List<Guitar> ALL_GUITARS = new ArrayList<>();
	
	static {
		ALL_GUITARS.add(GUITAR_1);
		ALL_GUITARS.add(GUITAR_2);
		ALL_GUITARS.add(GUITAR_3);
	}
}
